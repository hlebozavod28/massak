package ru.hlebozavod28.massak.restcontroller;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hlebozavod28.massak.DAO.MassaKScaleCrudRepository;
import ru.hlebozavod28.massak.DAO.MassaKWeighingCrudRepository;
import ru.hlebozavod28.massak.domain.MassaKWeighing;

@Log4j2
@RestController
public class MassaKController {
    @Autowired
    private MassaKScaleCrudRepository massaKScaleCrudRepository;
    @Autowired
    private MassaKWeighingCrudRepository massaKWeighingCrudRepository;

    @GetMapping("/getweight")
    String getwaught(@RequestParam(value = "workplace", defaultValue = "1") String workplace) {
        if (!workplace.equals("1")) {
            return "это так не работает.";
        }
        String retres = "";
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

    @GetMapping("/newhandcart")
    private String newHandCart(@RequestParam(value = "workplace") int workplace) {
        String ret = "1";
        String ipaddr = massaKScaleCrudRepository.getByWorkplace(workplace).get().getIpaddr();
        log.info("New hand cart in workplace " + workplace + " scale ip=" + ipaddr);
        ActiveXComponent scale = new ActiveXComponent("MassaKDriver100.Scales");
        Dispatch.put(scale, "Connection", new Variant(ipaddr + ":5001"));
        Dispatch.get(scale, "Connection");
        Variant oc = Dispatch.call(scale, "OpenConnection");
        if (oc.getInt() == 0) {
            Dispatch.call(scale, "ReadWeight");
            int st = Dispatch.get(scale, "Stable").getInt();
            int weight = Dispatch.get(scale, "Weight").getInt();
            log.info("weight=" + weight);
            massaKWeighingCrudRepository.save(new MassaKWeighing(weight, 0, workplace));
            ret = "0";
        } else {
            log.error("cant get data from scale " + ipaddr + " error=" + oc.getInt());
        }
        Dispatch.call(scale, "CloseConnection");
        return ret;

    }
}
