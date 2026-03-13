package com.tub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/bilhetica")
@CrossOrigin(origins = "*") // Permite que o browser fale com o Java sem bloqueios
public class BilheticaController {

    @GetMapping("/procura-oferta")
    public Map<String, Object> getProcuraOferta() {
        Map<String, Object> data = new HashMap<>();
        
        // Dados reais que vão fazer as barras azuis aparecer no teu gráfico!
        data.put("labels", Arrays.asList("06:00", "09:00", "12:00", "15:00", "18:00", "21:00"));
        data.put("procura", Arrays.asList(45, 180, 210, 190, 160, 90)); // As barras azuis
        data.put("oferta", Arrays.asList(100, 220, 240, 220, 100, 100));  // A linha verde
        
        return data;
    }
}