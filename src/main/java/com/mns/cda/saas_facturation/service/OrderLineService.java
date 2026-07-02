package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateOrderLineDTO;
import com.mns.cda.saas_facturation.Iservice.ICartService;
import com.mns.cda.saas_facturation.Iservice.IOrderLineService;
import com.mns.cda.saas_facturation.exception.InsufficientStockException;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.CartMapper;
import com.mns.cda.saas_facturation.mapper.OrderLineMapper;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Cart;
import com.mns.cda.saas_facturation.model.OrderLine;
import com.mns.cda.saas_facturation.repository.ArticleRepository;
import com.mns.cda.saas_facturation.repository.CartRepository;
import com.mns.cda.saas_facturation.repository.OrderLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService implements IOrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final OrderLineMapper orderLineMapper;
    private final CartMapper cartMapper;
    private final ICartService cartService;
    private final ArticleRepository articleRepository;

    private final CartRepository cartRepository;
    private final ArticleService articleService;

    @Override
    public OrderLineDTO findById(Long articleId, Long cartId) {
        OrderLine.OrderLineId key = new OrderLine.OrderLineId(articleId,cartId);
        return orderLineMapper.toDTO( orderLineRepository.findById(key)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Ligne de panier introuvable pour l'article avec l'id " + articleId + " et le panier avec l'id " + cartId)));
    }

    @Override
    public List<OrderLineDTO> findByCartId(Long cartId) {
        cartService.findById(cartId);

        return orderLineRepository.findByOrdLnId_CartId(cartId)
                .stream()
                .map(orderLineMapper::toDTO)
                .toList();
    }

    @Override
    public List<CartDTO> findByArtId(Long articleId) {
        articleService.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("L'article avec l'id " + articleId + " n'existe pas"));

        return orderLineRepository.findByOrdLnId_ArticleId(articleId)
                .stream()
                .map(OrderLine::getCart)
                .map(cartMapper::toDTO)
                .toList();
    }

    @Override
    public OrderLineDTO create(OrderLineRequestDTO dto) {
        Cart cart = cartRepository.findById(dto.cartId())
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " + dto.cartId() + " n'existe pas"));

        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new ResourceNotFoundException("L'article avec l'id " + dto.articleId() + " n'existe pas"));


        if(article.getArtStock() < dto.quantity()) throw
        new InsufficientStockException("La quantité en stock de l'article " + article.getArtName() + " n'est pas suffisante pour cette commande");

        OrderLine orderLine = new OrderLine(
                null,
                article,
                cart,
                dto.quantity()
        );

        return orderLineMapper.toDTO(orderLineRepository.save(orderLine));
    }

    @Override
    public OrderLineDTO update(Long artId, Long crtId, UpdateOrderLineDTO dto) {
        OrderLine.OrderLineId key = new OrderLine.OrderLineId(artId,crtId);
        OrderLine orderLine = orderLineRepository.findById(key)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Ligne de panier introuvable pour articleId=" + artId + ", cartId=" + crtId));


        if(orderLine.getArticle().getArtStock() < dto.quantity()) {
            throw
                    new InsufficientStockException("La quantité en stock de l'article " + orderLine.getArticle().getArtName() + " n'est pas suffisante pour cette commande");
        }

        orderLine.setOrdLnQuantity(dto.quantity());

        return orderLineMapper.toDTO(orderLineRepository.save(orderLine));
    }

    @Override
    public void delete(Long articleId, Long cartId) {
        OrderLine.OrderLineId key = new OrderLine.OrderLineId(articleId,cartId);
        OrderLine orderLine = orderLineRepository.findById(key)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Ligne de panier introuvable pour articleId=" + articleId + ", cartId=" + cartId));

        orderLineRepository.delete(orderLine);
    }
}
