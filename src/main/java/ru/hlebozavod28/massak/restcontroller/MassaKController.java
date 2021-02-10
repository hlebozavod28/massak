package ru.hlebozavod28.massak.restcontroller;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hlebozavod28.massak.DAO.*;
import ru.hlebozavod28.massak.domain.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
    private ProductCrudRepository productCrudRepository;


    @GetMapping("/inhandcart")
    private String inHandCart(@RequestParam(value = "workplace") long workplace_id,
                           @RequestParam(value = "handcart") long handcart_id,
                           @RequestParam(value = "productcode") int product_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("New hand cart in workplace " + workPlace.getWorkPlaceName());
        int retCode = 0;
        // cart motion
        var oldmotion = motionJpa.findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(workplace_id, handcart_id);
        for (Motion delmotion : oldmotion) {
            delmotion.setDeleted(true);
            log.info("delete wrong motion");
            motionJpa.save(delmotion);
        }
        Motion motion = motionJpa.save(new Motion(workplace_id, handcart_id, product_id,
                handcardSheetsJpa.getByHandcart(handcart_id).getSheets()));
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
    private ResponseEntity<String> outHandCart(@RequestParam(value = "workplace") long workplace_id, @RequestParam(value = "handcart") long handcart_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
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
            Product product = Optional.ofNullable(productCrudRepository.findById(Long.valueOf(motion.getProductCode())))
                    .orElseThrow(() -> new NoRecordException(Long.valueOf(motion.getProductCode()))).get();
            BigDecimal defectCount = new BigDecimal(defectWeight);
            log.info("defect weight=" + defectCount);
            BigDecimal oneWeight = product.getWeight().multiply(new BigDecimal(1000));
            log.info("one product weight=" + oneWeight);
            defectCount = defectCount.divide(oneWeight).setScale(0, RoundingMode.HALF_UP);
            log.info("defect count=" + defectCount);
            motion.setDefectCount(defectCount);
            BigDecimal productCount = new BigDecimal(product.getSheetalloc());
            log.info("product on sheet=" + productCount);
            productCount = productCount.multiply(new BigDecimal(motion.getSheets()));
            log.info("product count=" + productCount);
            productCount = productCount.subtract(defectCount);
            log.info("product count - defect=" + productCount);
            motion.setAmount(productCount);
            motionJpa.save(motion);
        }
        return ResponseEntity.ok(Integer.toString(retCode));
    }
    @GetMapping("/deletehadcart")
    private String deleteHandCart(@RequestParam(value = "workplace") long workplace_id,
                                @RequestParam(value = "handcart") long handcart_id) throws NoRecordException {
        // delete from weighting
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("Delete handcart from weighting " + workPlace.getWorkPlaceName());
        for (Scale sc : workPlace.getScales()) {
            String ipaddr = sc.getIpaddr();
            // find old weighting
            var oldWeighting = weightingCrudRepository.findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(sc.getId(), workPlace.getId(), false, false);
            for (Weighting wt : oldWeighting) {
                wt.setDeleted(true);
                weightingCrudRepository.save(wt);
                log.info(" scale=" + sc.getIpaddr());
            }
        }
        // delete from motions
        Motion motion2del = motionJpa.findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(workplace_id, handcart_id).orElseThrow(() -> new NoRecordException());
        log.info("delete handcart from motion");
        motion2del.setDeleted(true);
        motionJpa.save(motion2del);
        return "0";
    }

    @GetMapping("/changehadcart")
    private String deleteHandCart(@RequestParam(value = "workplace") long workplace_id,
                                @RequestParam(value = "handcart") long handcart_id,
                                @RequestParam(value = "sheets") int sheets) throws NoRecordException {
        Motion motionChange = motionJpa.findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(workplace_id, handcart_id).orElseThrow(() -> new NoRecordException());
        log.info("change sheet for workplace " + workplace_id + " and handcart " + handcart_id + " to " + sheets);
        motionChange.setSheets(sheets);
        motionJpa.save(motionChange);
        return "0";
    }

}

class NoRecordException extends Exception {
    NoRecordException(long recnum) {super("no record find " + recnum);}
    NoRecordException() {super("no record find");}
}
