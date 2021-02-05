package ru.hlebozavod28.massak.restcontroller;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hlebozavod28.massak.DAO.*;
import ru.hlebozavod28.massak.domain.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
public class MassaKController {
    @Autowired
    private WorkplaceCrudRepository workplaceCrudRepository;
    @Autowired
    private WeightingCrudRepository weightingCrudRepository;
    @Autowired
    private MotionCrudRepository motionCrudRepository;
    @Autowired
    private HandcardSheetsCrudRepository handcardSheetsCrudRepository;
    @Autowired
    private ProductCrudRepository productCrudRepository;


    @GetMapping("/inhandcart")
    private void inHandCart(@RequestParam(value = "workplace") long workplace_id, @RequestParam(value = "handcart") long handcart_id,
                            @RequestParam(value = "productcode") int product_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("New hand cart in workplace " + workPlace.getWorkPlaceName());
        // cart motion
        var oldmotion = motionCrudRepository.findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(workplace_id, handcart_id);
        for (Motion delmotion : oldmotion) {
            delmotion.setDeleted(true);
            log.info("delete wrong motion");
            motionCrudRepository.save(delmotion);
        }
        Motion motion = motionCrudRepository.save(new Motion(workplace_id, handcart_id, product_id,
                handcardSheetsCrudRepository.getByHandcart(handcart_id).getSheets()));
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
                }
                int weight = Dispatch.get(scale, "Weight").getInt();
                log.info("  weight=" + weight);
                weightingCrudRepository.save(new Weighting(weight, sc.getId(), workPlace.getId()));
            } else {
                log.error("cant get data from scale " + ipaddr + " error=" + oc.getInt());
            }
            Dispatch.call(scale, "CloseConnection");
        }
    }


    @GetMapping("/outhandcart")
    private void outHandCart(@RequestParam(value = "workplace") long workplace_id, @RequestParam(value = "handcart") long handcart_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("Out handcart from workolace " + workPlace.getWorkPlaceName());
        // write to motion
        var newmotion = motionCrudRepository.findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(workplace_id, handcart_id);
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
            defectCount = defectCount.divide(oneWeight);
            log.info("defect count=" + defectCount);
            motion.setDefectCount(defectCount);
            BigDecimal productCount = new BigDecimal(product.getSheetalloc());
            log.info("product on sheet=" + productCount);
            productCount = productCount.multiply(new BigDecimal(motion.getSheets()));
            log.info("product count=" + productCount);
            productCount = productCount.subtract(defectCount);
            log.info("product count - defect=" + productCount);
            motion.setAmount(productCount);
            motionCrudRepository.save(motion);
        }
    }
    @GetMapping("/deletehadcart")
    private void deleteHandCart(@RequestParam(value = "workplace") long workplace_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("Delete handcart from workolace " + workPlace.getWorkPlaceName());
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
    }
}

class NoRecordException extends Exception {
    NoRecordException(long recnum) {
        super("no record find " + recnum);
    }
}
