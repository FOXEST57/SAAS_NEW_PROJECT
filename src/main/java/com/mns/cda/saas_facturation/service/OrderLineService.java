package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateOrderLineDTO;
import com.mns.cda.saas_facturation.Iservice.ICartService;
import com.mns.cda.saas_facturation.Iservice.IOrderLineService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.OrderLineMapper;
import com.mns.cda.saas_facturation.model.Cart;
import com.mns.cda.saas_facturation.model.OrderLine;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.OrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService implements IOrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final OrderLineMapper orderLineMapper;
    private final ICartService cartService;
    private final ArticleRepository articleRepository;

    @Override
    public OrderLineDTO findById(Long artId, Long crtId) {
        OrderLine.OrderLineId key = new OrderLine.OrderLineId(artId,crtId);
        return orderLineMapper.toDTO( orderLineRepository.findById(key)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Ligne de panier introuvable pour articleId=" + artId + ", cartId=" + crtId)));
    }

    @Override
    public List<OrderLineDTO> findByCartId(Long crtId) throws ICartService.CartNotFoundException {
        Cart cart = cartService.findById(crtId);

        return orderLineRepository.findByOrdLnId_CartId(crtId)
                .stream()
                .map(orderLineMapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderLineDTO> findByArtId(Long artId) {
        return List.of();
    }

    @Override
    public OrderLineDTO create(OrderLineRequestDTO dto) {
        return null;
    }

    @Override
    public OrderLineDTO update(Long artId, Long crtId, UpdateOrderLineDTO dto) {
        return null;
    }

    @Override
    public void delete(Long artId, Long cartId) {

    }
}
