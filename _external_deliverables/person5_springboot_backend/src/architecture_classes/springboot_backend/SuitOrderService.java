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
 * SERVIÇO DE NEGÓCIOS (Spring Service layer)
 * Contém a lógica de processamento, validação e gestão transacional dos pedidos.
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
                    System.err.println("Erro ao salvar pedido sincronizado " + order.getId() + " - " + e.getMessage());
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
