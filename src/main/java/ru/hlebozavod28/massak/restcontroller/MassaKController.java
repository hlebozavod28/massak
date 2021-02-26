package ru.hlebozavod28.massak.restcontroller;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hlebozavod28.massak.DAO.*;
import ru.hlebozavod28.massak.domain.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class MassaKController {
    @Autowired
    private WorkplaceCrudRepository workplaceCrudRepository;
    @Autowired
    private WeightingCrudRepository weightingCrudRepository;
    @Autowired
    private MotionJpa motionJpa;
    @Autowired
    private HandcardSheetsJpa handcardSheetsJpa;
    @Autowired
    private ProductJpa productJpa;
    @Autowired
    private ProdExecJpa prodExecJpa;

    @Value("${hlebozavod28.handcartdefaultsheets}")
    private String defaultSheetsStr;
    @Value("${hlebozavod28.smenastarttime}")
    private  String smenaStartTime;
    @Value("${hlebozavod28.smenaendtime}")
    private  String smenaEndTime;
    @Value("${hlebozavod28.smenaendhours}")
    private int smenaEndHours;


    @GetMapping("/inhandcart")
    private String inHandCart(@RequestParam(value = "workplace") long workplace_id,
                           @RequestParam(value = "handcart") long handcart_id,
                           @RequestParam(value = "productcode") int product_id) throws InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(NoRecordFindException::new);
        log.info("New hand cart in workplace " + workPlace.getWorkPlaceName());
        int retCode = 0;
        // cart motion
        var oldmotion = motionJpa.findByWorkplaceIdAndAmountNullAndDeletedFalse(workplace_id);
        for (Motion delmotion : oldmotion) {
            delmotion.setDeleted(true);
            log.info("delete wrong motion id=" + delmotion.getId());
            motionJpa.save(delmotion);
        }
        int defaultSheetsInt = Integer.parseInt(defaultSheetsStr);
        motionJpa.save(new Motion(workplace_id, handcart_id, product_id,
                handcardSheetsJpa.getByHandcart(handcart_id).orElse(new HandcardSheets(defaultSheetsInt)).getSheets()));
        // weighting
        for (Scale sc : workPlace.getScales()) {
            log.info(" scale=" + sc.getIpaddr());
            // find old non completed
            var oldBadWeighting = weightingCrudRepository.findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(sc.getId(), workPlace.getId(), false, false);
            for (Weighting wt : oldBadWeighting) {
                wt.setDeleted(true);
                weightingCrudRepository.save(wt);
                log.info(" deleting empty weighting");
            }
            String ipaddr = sc.getIpaddr();
            ActiveXComponent scale = new ActiveXComponent("MassaKDriver100.Scales");
            Dispatch.put(scale, "Connection", new Variant(ipaddr));
            Variant oc = Dispatch.call(scale, "OpenConnection");
            if (oc.getInt() == 0) {
                Dispatch.call(scale, "ReadWeight");
                int st = Dispatch.get(scale, "Stable").getInt();
                if (st==0) {
                    log.info("  no stable, wait");
                    TimeUnit.SECONDS.sleep(2);
                    retCode = 100;
                }
                int weight = Dispatch.get(scale, "Weight").getInt();
                log.info("  weight=" + weight);
                weightingCrudRepository.save(new Weighting(weight, sc.getId(), workPlace.getId()));
            } else {
                log.error("cant get data from scale " + ipaddr + " error=" + oc.getInt());
                retCode = oc.getInt();
            }
            Dispatch.call(scale, "CloseConnection");
        }
        return Integer.toString(retCode);
    }


    @GetMapping("/outhandcart")
    private ResponseEntity<String> outHandCart(@RequestParam(value = "workplace") long workplace_id,
                                               @RequestParam(value = "handcart") long handcart_id) throws InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(NoRecordFindException::new);
        log.info("Out handcart from workolace " + workPlace.getWorkPlaceName());
        int retCode = 0;
        // write to motion
        var newmotion = motionJpa.findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(workplace_id, handcart_id);
        for (Motion motion : newmotion) {
            motion.setOutTs(new Timestamp(System.currentTimeMillis()));
            int defectWeight = 0;
            for (Scale sc : workPlace.getScales()) {
                log.info(" scale=" + sc.getIpaddr());
                String ipaddr = sc.getIpaddr();
                ActiveXComponent scale = new ActiveXComponent("MassaKDriver100.Scales");
                Dispatch.put(scale, "Connection", new Variant(ipaddr));
                Variant oc = Dispatch.call(scale, "OpenConnection");
                if (oc.getInt() == 0) {
                    Dispatch.call(scale, "ReadWeight");
                    int st = Dispatch.get(scale, "Stable").getInt();
                    if (st == 0) {
                        log.info("  no stable, wait");
                        TimeUnit.SECONDS.sleep(2);
                        retCode = 100;
                    }
                    int weight = Dispatch.get(scale, "Weight").getInt();
                    log.info("  weight=" + weight);
                    // find old weighting
                    var oldWeighting = weightingCrudRepository.findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(sc.getId(), workPlace.getId(), false, false);
                    for (Weighting wt : oldWeighting) {
                        wt.setFinalWeight(weight);
                        wt.setCompleted(true);
                        weightingCrudRepository.save(wt);
                        defectWeight += wt.getFinalWeight() - wt.getInitialWeight();
                    }
                } else {
                    log.error("cant get data from scale " + ipaddr + " error=" + oc.getInt());
                    retCode = oc.getInt();
                }
                Dispatch.call(scale, "CloseConnection");
            }

            // calculate amount
            Product product = productJpa.findById(motion.getProductCode()).orElseThrow(NoRecordFindException::new);
            BigDecimal defectCount = new BigDecimal(defectWeight);
            log.info("defect weight=" + defectCount);
            BigDecimal oneWeight = product.getWeightDough().multiply(new BigDecimal(1000));
            log.info("one product weight=" + oneWeight);
            defectCount = defectCount.divide(oneWeight, 0, RoundingMode.HALF_UP);
            log.info("defect count=" + defectCount);
            motion.setDefectCount(defectCount);
            recalcMotion(motion);
        }
        return ResponseEntity.ok(Integer.toString(retCode));
    }
    @GetMapping("/deletehadcart")
    private String deleteHandCart(@RequestParam(value = "workplace") long workplace_id,
                                @RequestParam(value = "handcart") long handcart_id) {
        // delete from weighting
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(NoRecordFindException::new);
        log.info("Delete handcart from weighting " + workPlace.getWorkPlaceName());
        for (Scale sc : workPlace.getScales()) {
            // find old weighting
            var oldWeighting = weightingCrudRepository.findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(sc.getId(), workPlace.getId(), false, false);
            for (Weighting wt : oldWeighting) {
                wt.setDeleted(true);
                weightingCrudRepository.save(wt);
                log.info(" scale=" + sc.getIpaddr());
            }
        }
        // delete from motions
        log.info("delete handcart from motion workplace= " + workplace_id + " handcart=" + handcart_id);
        Motion motion2del;
        if (handcart_id == 0) {
            motion2del = motionJpa.findFirstByWorkplaceIdAndDeletedFalseOrderByIdDesc(workplace_id).orElseThrow(NoRecordFindException::new);
        } else {
            motion2del = motionJpa.findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(workplace_id, handcart_id).orElseThrow(NoRecordFindException::new);
        }
        motion2del.setDeleted(true);
        recalcMotion(motion2del);
        return "0";
    }

    @GetMapping("/changehadcart")
    private String deleteHandCart(@RequestParam(value = "workplace") long workplace_id,
                                @RequestParam(value = "handcart") long handcart_id,
                                @RequestParam(value = "sheets") int sheets) {
        Motion motionChange = motionJpa.findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(workplace_id, handcart_id).orElseThrow(NoRecordFindException::new);
        log.info("change sheet for workplace " + workplace_id + " and handcart " + handcart_id + " to " + sheets + " (id=" + motionChange.getId() + ")");
        motionChange.setSheets(sheets);
        if (motionChange.getAmount() == null) {
            motionJpa.save(motionChange);
        } else {
            recalcMotion(motionChange);
        }
        return "0";
    }

    private void recalcMotion(Motion motion) {
        Optional<BigDecimal> oldAmountOpt = Optional.ofNullable(motion.getAmount());
        BigDecimal oldAmount = oldAmountOpt.orElse(new BigDecimal("0.0"));
        var amount = new BigDecimal("0.0");
        Product product = productJpa.findById(motion.getProductCode()).orElseThrow(NoRecordFindException::new);
        if (!motion.isDeleted()) {
            BigDecimal prodOfSheet = new BigDecimal(product.getSheetAlloc());
            var sheets = new BigDecimal(motion.getSheets());
            BigDecimal defectCount = motion.getDefectCount();
            log.info("amount=" + sheets + " * " + prodOfSheet + " - " + defectCount + " = " + amount);
            amount = prodOfSheet.multiply(sheets).subtract(defectCount);
            motion.setAmount(amount);
            motionJpa.save(motion);
            oldAmount = amount.subtract(oldAmount);
        } else {
            oldAmount = new BigDecimal("0.0").subtract(motion.getDefectCount()).subtract(oldAmount);
            log.info("delete, amount=" + oldAmount);
        }
        motionJpa.flush();
        log.info(" prodexec=" + oldAmount);
        LocalDateTime localDateTime = motion.getInTs().toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        LocalDate smenaDate = localDateTime.plusHours(smenaEndHours).toLocalDate();
        LocalTime smenaTime = localDateTime.toLocalTime();
        LocalTime startTime = LocalTime.parse(smenaStartTime);
        LocalTime endTime = LocalTime.parse(smenaEndTime);
        int smena = 1;
        if (smenaTime.isAfter(startTime) & smenaTime.isBefore(endTime)) {
            smena = 2;
        }
        ProdExec prodExec = prodExecJpa.findFirstByProdDateAndProdSmenaAndProdId(smenaDate, smena, motion.getProductCode())
                .orElse(new ProdExec(smenaDate, smena, motion.getProductCode()));
        prodExec.setNumberLineItems(prodExec.getNumberLineItems() + oldAmount.intValueExact());
        BigDecimal prodKg = product.getWeightDough().multiply(new BigDecimal(prodExec.getNumberLineItems()));
        prodExec.setNumberLineKg(prodKg);
        prodExecJpa.save(prodExec);
    }
}
