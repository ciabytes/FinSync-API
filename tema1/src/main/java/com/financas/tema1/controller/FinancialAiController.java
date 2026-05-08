package com.financas.tema1.controller;

import com.financas.tema1.ai.AiInsightRequest;
import com.financas.tema1.ai.AiInsightResponse;
import com.financas.tema1.domain.User;
import com.financas.tema1.repository.UserRepository;
import com.financas.tema1.service.FinancialAiService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ai")
public class FinancialAiController {

    private final FinancialAiService financialAiService;
    private final UserRepository userRepository;

    public FinancialAiController(FinancialAiService financialAiService, UserRepository userRepository) {
        this.financialAiService = financialAiService;
        this.userRepository = userRepository;
    }

    @PostMapping("/insights")
    public AiInsightResponse insights(
            @RequestBody AiInsightRequest request,
            Authentication authentication) {

        User user = currentUser(authentication);
        String question = validateQuestion(request);

        return financialAiService.answer(user.getId(), question);
    }

    @GetMapping("/summary/last30days")
    public AiInsightResponse getSummaryLast30Days(Authentication authentication) {
        User user = currentUser(authentication);

        String question = "Faça um resumo detalhado da minha movimentação financeira dos últimos 30 dias, "
                + "incluindo padrões de gastos, categorias principais e recomendações.";

        return financialAiService.answerLast30Days(user.getId(), question);
    }

    @GetMapping("/insights/categories")
    public AiInsightResponse getCategoriesInsights(Authentication authentication) {
        User user = currentUser(authentication);

        String question = "Analise meus gastos por categoria e identifique onde posso economizar.";

        return financialAiService.answer(user.getId(), question);
    }

    private User currentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private String validateQuestion(AiInsightRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A pergunta é obrigatória");
        }

        return request.question().trim();
    }
}
