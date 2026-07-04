package mz.ac.unizambeze.suitup.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR DE MODELOS DE FATOS (GET /api/models)
 * Disponibiliza as opções de modelos, caimentos e estilos suportados pela máquina de produção da alfaiataria.
 */
@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "*")
public class ModelController {

    /**
     * GET /api/models
     * Retorna a lista detalhada de cortes e designs que o utilizador pode escolher no simulador.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAvailableModels() {
        List<Map<String, Object>> modelsList = new ArrayList<>();

        // 1. Classic Slim-Fit
        Map<String, Object> model1 = new HashMap<>();
        model1.put("style", "Classic Slim-Fit");
        model1.put("ratio", "100% lã merino");
        model1.put("desc", "Corte contemporâneo estreito nos ombros e cintura para uma silhueta alongada e elegante.");
        model1.put("tag", "Lã Fina Italiana — Tecido Super 120s");
        modelsList.add(model1);

        // 2. Modern Double-Breasted
        Map<String, Object> model2 = new HashMap<>();
        model2.put("style", "Modern Double-Breasted");
        model2.put("ratio", "95% lã merino, 5% seda");
        model2.put("desc", "Estilo trespassado clássico redesenhado para o executivo dinâmico, combinando imponência e caimento justo.");
        model2.put("tag", "Lã Inglesa York com Acabamento Acetinado");
        modelsList.add(model2);

        // 3. Zambeze Imperial Premium
        Map<String, Object> model3 = new HashMap<>();
        model3.put("style", "Zambeze Imperial Premium");
        model3.put("ratio", "100% Seda de Linho Nobre");
        model3.put("desc", "O expoente máximo da coleção. Fato sob medida ultra leve concebido para o clima tropical africano com forro de seda real.");
        model3.put("tag", "Tecido Nobre Termo-Ativo de Edição Limitada");
        modelsList.add(model3);

        return ResponseEntity.ok(modelsList);
    }
}
