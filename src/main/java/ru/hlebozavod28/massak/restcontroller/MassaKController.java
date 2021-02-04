package ru.hlebozavod28.massak.restcontroller;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hlebozavod28.massak.DAO.WeightingCrudRepository;
import ru.hlebozavod28.massak.DAO.WorkplaceCrudRepository;
import ru.hlebozavod28.massak.domain.Scale;
import ru.hlebozavod28.massak.domain.Weighting;

import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
public class MassaKController {
    @Autowired
    private WorkplaceCrudRepository workplaceCrudRepository;
    @Autowired
    private WeightingCrudRepository weightingCrudRepository;

    @GetMapping("/getweight")
    String getwaught(@RequestParam(value = "workplace", defaultValue = "1") String workplace) {
        if (!workplace.equals("1")) {
            return "это так не работает.";
        }
        String retres;
        ActiveXComponent scale = new ActiveXComponent("MassaKDriver100.Scales");
        Dispatch.put(scale, "Connection", new Variant("10.1.232.8:5001"));
        Dispatch.get(scale, "Connection");
        Variant oc = Dispatch.call(scale, "OpenConnection");
        if (oc.getInt() == 0) {
            Dispatch.call(scale, "ReadWeight");
            int st = Dispatch.get(scale, "Stable").getInt();
            int weight = Dispatch.get(scale, "Weight").getInt();
            retres = Integer.toString(weight);
            if (st != 1) {
                retres = retres + ", но это не точно.";
            }
        } else {
            retres = "ERROR connect " + oc.getInt();
        }
        Dispatch.call(scale, "CloseConnection");
        return retres;
    }

    @GetMapping("/inhandcart")
    private void inHandCart(@RequestParam(value = "workplace") long workplace_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("New hand cart in workplace " + workPlace.getWorkPlaceName());
        for (Scale sc : workPlace.getScales()) {
            log.info(" весы=" + sc.getIpaddr());
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


    @GetMapping("/outhadcart")
    private void outHandCart(@RequestParam(value = "workplace") long workplace_id) throws NoRecordException, InterruptedException {
        var workPlace = workplaceCrudRepository.findById(workplace_id).orElseThrow(() -> new NoRecordException(workplace_id));
        log.info("Out handcart from workolace " + workPlace.getWorkPlaceName());
        for (Scale sc : workPlace.getScales()) {
            log.info(" весы=" + sc.getIpaddr());
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
            } else {
                log.error("cant get data from scale " + ipaddr + " error=" + oc.getInt());
            }
            Dispatch.call(scale, "CloseConnection");
        }
    }
}

class NoRecordException extends Exception {
    NoRecordException(long recnum) {
        super("no record find " + recnum);
    }
}
