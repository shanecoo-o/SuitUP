package mz.ac.unizambeze.suitup.backend.service;

import mz.ac.unizambeze.suitup.backend.entity.SuitOrderEntity;
import mz.ac.unizambeze.suitup.backend.repository.SuitOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SERVIÇO DE PROCESSAMENTO GERAL (Business layer)
 * Gere as encomendas, armazena os uploads e atualiza os estados de pagamento e de confecção física.
 */
@Service
public class SuitOrderService {

    private final SuitOrderRepository orderRepository;

    @Autowired
    public SuitOrderService(SuitOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retorna todos os pedidos da base de dados central
     */
    public List<SuitOrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Procura um pedido individual pelo ID público de tracking
     */
    public Optional<SuitOrderEntity> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    /**
     * Regista um novo pedido validando se o código M-Pesa já foi usado
     */
    @Transactional
    public SuitOrderEntity createOrder(SuitOrderEntity order) {
        if (orderRepository.findByMpesaTransactionId(order.getMpesaTransactionId()).isPresent()) {
            throw new IllegalArgumentException("O ID de Transação M-Pesa indicado já foi resgatado para outra encomenda.");
        }
        order.setCreatedAtDate(LocalDateTime.now());
        if (order.getStatusStr() == null) {
            order.setStatusStr("Design Concluído"); // Estado inicial do fluxo de produção físico
        }
        return orderRepository.save(order);
    }

    /**
     * Associa o caminho de imagem do comprovativo M-Pesa em multipart à encomenda criada.
     */
    @Transactional
    public SuitOrderEntity savePaymentProofUrl(String orderId, String relativePath) {
        SuitOrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma encomenda localizada para asociar o comprovante físico: " + orderId));
        
        order.setPaymentProofUrl(relativePath);
        return orderRepository.save(order);
    }

    /**
     * Validação Administrativa de Pagamento M-Pesa
     * Executada pelo Alfaiate Chefe após conferir a transação em Moçambique.
     */
    @Transactional
    public SuitOrderEntity validatePaymentAndProgress(String orderId, boolean isValid, String notes) {
        SuitOrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Erro ao localizar ID da Encomenda no back-office: " + orderId));
        
        order.setPaymentValidated(isValid);
        order.setAdminNotes(notes);
        
        if (isValid) {
            order.setStatusStr("Medidas & Corte"); // Avança para a segunda fase de fabrico automaticamente
        } else {
            order.setStatusStr("Pagamento Rejeitado"); // Sinaliza falha para o cliente ver no app reativo
        }
        
        return orderRepository.save(order);
    }

    /**
     * Sincroniza em lote ("Store and Forward") múltiplos pedidos que foram efetuados offline
     */
    @Transactional
    public void syncOfflineOrders(List<SuitOrderEntity> offlineOrders) {
        for (SuitOrderEntity order : offlineOrders) {
            // Se já existir na base, pula ou atualiza apenas se necessário.
            // Se for novo, persiste aplicando as regras.
            if (!orderRepository.existsById(order.getId())) {
                try {
                    createOrder(order);
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro ao salvar pedido de sincronização: " + order.getId() + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Atualiza o estado físico do tracking do fato (Mecanismo para o painel de verificação Back-Office)
     */
    @Transactional
    public SuitOrderEntity updateOrderStatus(String orderId, String newStatus) {
        SuitOrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Encomenda não localizada com o ID: " + orderId));
        
        order.setStatusStr(newStatus);
        return orderRepository.save(order);
    }
}
