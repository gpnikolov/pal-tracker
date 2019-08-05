package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    String port, mlimit, cfind, cfaddr;
    public EnvController (@Value("${port:NOT SET}") String port,
                          @Value("${memory.limit:NOT SET}") String mlimit,
                          @Value("${cf.instance.index:NOT SET}") String cfind,
                          @Value("${cf.instance.addr:NOT SET}") String cfaddr) {

        this.port=port;
        this.mlimit= mlimit;
        this.cfind = cfind;
        this.cfaddr=cfaddr;

    }


    @GetMapping("/env")
    public Map<String, String> getEnv() {
        HashMap<String, String> envMap = new HashMap<>();
        envMap.put("PORT", port);
        envMap.put("MEMORY_LIMIT", mlimit);
        envMap.put("CF_INSTANCE_INDEX", cfind);
        envMap.put("CF_INSTANCE_ADDR", cfaddr);
        return envMap;
    }
}
