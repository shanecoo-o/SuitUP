package com.suitup.app.data.mapper

import com.suitup.app.data.remote.auth.UserDto
import com.suitup.app.data.remote.auth.UserRoleDto
import com.suitup.app.data.remote.catalog.SuitModelDto
import com.suitup.app.data.remote.dashboard.AdminDashboardDto
import com.suitup.app.data.remote.orders.FulfillmentTypeDto
import com.suitup.app.data.remote.orders.MeasurementDto
import com.suitup.app.data.remote.orders.OrderDto
import com.suitup.app.data.remote.orders.OrderItemDto
import com.suitup.app.data.remote.orders.OrderStatusDto
import com.suitup.app.data.remote.orders.OrderStatusHistoryDto
import com.suitup.app.data.remote.payments.PaymentDto
import com.suitup.app.data.remote.payments.PaymentStatusDto
import com.suitup.app.domain.model.AdminDashboardSummary
import com.suitup.app.domain.model.AdminOrderSummary
import com.suitup.app.domain.model.AdminPaymentSummary
import com.suitup.app.domain.model.AppUserRole
import com.suitup.app.domain.model.AuthenticatedUser
import com.suitup.app.domain.model.CorFato
import com.suitup.app.domain.model.DadosClientePedido
import com.suitup.app.domain.model.DesignFato
import com.suitup.app.domain.model.EnderecoEntrega
import com.suitup.app.domain.model.EstadoEvento
import com.suitup.app.domain.model.EstadoPedido
import com.suitup.app.domain.model.EventoPedido
import com.suitup.app.domain.model.InfoPagamento
import com.suitup.app.domain.model.Medidas
import com.suitup.app.domain.model.MetodoPagamento
import com.suitup.app.domain.model.PartesFato
import com.suitup.app.domain.model.PaymentRecord
import com.suitup.app.domain.model.PaymentStatus
import com.suitup.app.domain.model.Pedido
import com.suitup.app.domain.model.PontoLevantamento
import com.suitup.app.domain.model.SuitModel
import com.suitup.app.domain.model.Tecido
import com.suitup.app.domain.model.TipoEntrega
import com.suitup.app.domain.model.Utilizador
import kotlin.math.roundToInt

fun UserDto.toDomain(): Utilizador = Utilizador(
    id = id,
    nome = fullName,
    email = email,
    telefone = phone.orEmpty(),
)

fun UserDto.toAuthenticatedUser(): AuthenticatedUser = AuthenticatedUser(
    profile = toDomain(),
    roles = roles.mapTo(mutableSetOf()) { role ->
        when (role) {
            UserRoleDto.ADMIN -> AppUserRole.ADMIN
            UserRoleDto.CUSTOMER -> AppUserRole.CUSTOMER
        }
    },
)

fun SuitModelDto.toDomain(): SuitModel = SuitModel(
    id = id,
    name = name,
    category = category,
    description = description,
    basePrice = price.roundToInt(),
    imageKey = imageKey ?: "suit_classic_black",
    fabricType = fabricType,
    color = color,
    available = active,
)

fun PaymentDto.toDomain(): PaymentRecord = PaymentRecord(
    id = id,
    orderId = orderId,
    method = method.name,
    status = status.toDomain(),
    amountMt = amount.roundToInt(),
    currency = currency,
    transactionReference = transactionReference,
    proofFileId = proofFileId,
    submittedAt = submittedAt,
    confirmedAt = confirmedAt,
    rejectedAt = rejectedAt,
    reviewedByUserId = reviewedByUserId,
    rejectionReason = rejectionReason,
)

fun OrderDto.toDomain(): Pedido {
    val domainStatus = toDomainOrderStatus(status, paymentStatus)
    val timeline = statusHistory.mapIndexed { index, event ->
        event.toDomain(index == statusHistory.lastIndex)
    }.ifEmpty {
        listOf(EventoPedido(domainStatus, EstadoEvento.Actual, updatedAt))
    }
    val latestPayment = payments.firstOrNull()
    return Pedido(
        id = id,
        numero = orderNumber,
        idUtilizador = customerUserId.orEmpty(),
        cliente = DadosClientePedido(
            nome = customerName,
            email = customerEmail.orEmpty(),
            telefone = customerPhone,
        ),
        medidas = measurement?.toDomain(),
        designsFato = items.map(OrderItemDto::toDomain),
        subtotal = subtotalAmount.roundToInt(),
        taxaEntrega = deliveryFee.roundToInt(),
        total = totalAmount.roundToInt(),
        tipoEntrega = if (fulfillmentType == FulfillmentTypeDto.DELIVERY) {
            TipoEntrega.Entrega
        } else {
            TipoEntrega.Levantamento
        },
        enderecoEntrega = deliveryAddress?.let {
            EnderecoEntrega(cidade = "", bairro = "", rua = it)
        },
        pontoLevantamento = pickupLocation?.let {
            PontoLevantamento(id = "remote-pickup", nome = it, endereco = it)
        },
        pagamento = InfoPagamento(
            metodo = MetodoPagamento.MpesaManual,
            caminhoImagemComprovativo = latestPayment?.proofFileId,
            numeroMpesa = latestPayment?.transactionReference.orEmpty(),
            titular = customerName,
            status = paymentStatus.toDomain(),
            referenciaTransaccao = latestPayment?.transactionReference,
        ),
        estado = domainStatus,
        linhaTempo = timeline,
        criadoEm = createdAt,
        actualizadoEm = updatedAt,
    )
}

fun AdminDashboardDto.toDomain(): AdminDashboardSummary = AdminDashboardSummary(
    totalOrders = totalOrders.toInt(),
    activeSuitModels = activeSuitModels.toInt(),
    inactiveSuitModels = inactiveSuitModels.toInt(),
    pendingPayments = pendingPayments.toInt(),
    confirmedPayments = confirmedPayments.toInt(),
    rejectedPayments = rejectedPayments.toInt(),
    confirmedRevenueMt = confirmedRevenue.roundToInt(),
    ordersByStatus = ordersByStatus.mapValues { it.value.toInt() },
    recentOrders = recentOrders.map { item ->
        AdminOrderSummary(
            id = item.id,
            orderNumber = item.orderNumber,
            customerName = item.customerName,
            customerPhone = item.customerPhone,
            status = item.status.name,
            paymentStatus = item.paymentStatus.toDomain(),
            totalMt = item.totalAmount.roundToInt(),
            createdAt = item.createdAt,
        )
    },
    recentPendingPayments = recentPendingPayments.map { item ->
        AdminPaymentSummary(
            paymentId = item.paymentId,
            orderId = item.orderId,
            orderNumber = item.orderNumber,
            customerName = item.customerName,
            method = item.method.name,
            status = item.status.toDomain(),
            amountMt = item.amount.roundToInt(),
            transactionReference = item.transactionReference,
            submittedAt = item.submittedAt,
        )
    },
)

private fun OrderItemDto.toDomain(): DesignFato = DesignFato(
    id = id,
    idModeloBase = suitModelId.orEmpty(),
    nome = suitName,
    partes = PartesFato(),
    tecido = Tecido(id = fabric.lowercase().replace(' ', '_'), nome = fabric, hexAmostra = "#2B2B2B"),
    cor = CorFato(id = color.lowercase().replace(' ', '_'), nome = color, hex = "#1C1C1C"),
    preco = unitPrice.roundToInt(),
)

private fun MeasurementDto.toDomain(): Medidas = Medidas(
    alturaCm = heightCm.toFormValue(),
    ombrosCm = shouldersCm.toFormValue(),
    peitoCm = chestCm.toFormValue(),
    cinturaCm = waistCm.toFormValue(),
    quadrilCm = hipCm?.toFormValue().orEmpty(),
    mangaCm = sleeveCm.toFormValue(),
    calcaCm = trouserLengthCm.toFormValue(),
    pescocoCm = neckCm?.toFormValue().orEmpty(),
    observacoes = notes.orEmpty(),
)

private fun OrderStatusHistoryDto.toDomain(isLatest: Boolean): EventoPedido = EventoPedido(
    estadoPedido = toDomainOrderStatus(newStatus, PaymentStatusDto.CONFIRMED),
    estadoEvento = if (isLatest) EstadoEvento.Actual else EstadoEvento.Concluido,
    ocorridoEm = createdAt,
)

private fun toDomainOrderStatus(
    status: OrderStatusDto,
    paymentStatus: PaymentStatusDto,
): EstadoPedido = when {
    status == OrderStatusDto.CANCELLED -> EstadoPedido.Cancelado
    status == OrderStatusDto.DELIVERED -> EstadoPedido.Entregue
    status == OrderStatusDto.READY_FOR_DELIVERY -> EstadoPedido.ProntoParaEntrega
    status == OrderStatusDto.IN_PRODUCTION -> EstadoPedido.EmProducao
    paymentStatus == PaymentStatusDto.REJECTED -> EstadoPedido.PagamentoRejeitado
    paymentStatus == PaymentStatusDto.CONFIRMED -> EstadoPedido.PagamentoValidado
    else -> EstadoPedido.AguardandoPagamento
}

private fun PaymentStatusDto.toDomain(): PaymentStatus = when (this) {
    PaymentStatusDto.PENDING -> PaymentStatus.PENDING
    PaymentStatusDto.CONFIRMED -> PaymentStatus.CONFIRMED
    PaymentStatusDto.REJECTED -> PaymentStatus.REJECTED
}

private fun Double.toFormValue(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()
