package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchCartStatus;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.ICartService;
import com.mns.cda.saas_facturation.cart.mapper.CartPipelineMapper;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import com.mns.cda.saas_facturation.cart.service.pipeline.CartValidatedEvent;
import com.mns.cda.saas_facturation.enumeration.CartStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.cart.mapper.CartMapper;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.cart.model.Cart;
import com.mns.cda.saas_facturation.referencement.ReferenceCounterService;
import com.mns.cda.saas_facturation.referencement.ReferenceType;
import com.mns.cda.saas_facturation.user.model.Customer;
import com.mns.cda.saas_facturation.cart.model.OrderLine;
import com.mns.cda.saas_facturation.product.repository.ArticleRepository;
import com.mns.cda.saas_facturation.cart.repository.CartRepository;
import com.mns.cda.saas_facturation.user.repository.CustomerRepository;
import com.mns.cda.saas_facturation.cart.repository.OrderLineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ArticleRepository  articleRepository;
    private final OrderLineRepository orderLineRepository;
    private final CartPipelineMapper cartPipelineMapper;
    private final QuoteRepository quoteRepository;

    private final ReferenceCounterService referenceCounterService;
    private final ApplicationEventPublisher publisher;

    @Override
    public List<CartDTO> findAll() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::toDTO)
                .toList();
    }

    @Override
    public CartDTO findById(Long id) {
        return cartRepository.findById(id).map(cartMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" ));

    }

    @Override
    public CartDTO create(Customer creator, CartRequestDTO dto) {

        Quote qotParent = dto.parentQuoteId() != null
                ? quoteRepository.findById(dto.parentQuoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Devis non existant"))
                : null;

        Cart cart = new Cart();
        cart.setCrtRef(referenceCounterService
                .generateReference(creator.getCorporation(), ReferenceType.CART));
        cart.setCrtStatus(CartStatus.OPEN);

        cart.setReceiverEmail(dto.receiverEmail());
        cart.setParentQuoteId(qotParent != null ? qotParent.getQotId() : null);

        cart.setCreator(creator);
        cartRepository.save(cart);

        if (dto.orderLines() != null && !dto.orderLines().isEmpty()) {
            for (OrderLineRequestDTO orderLine : dto.orderLines()) {

                Article article = articleRepository.findById(orderLine.articleId())
                        .orElseThrow(() -> new ResourceNotFoundException("L'article avec l'id " + orderLine.articleId() + " n'existe pas"));

                if(article.isActive() != true){
                    throw new IllegalStateException("L'article avec l'id " + orderLine.articleId() + " n'est pas actif");
                }

                OrderLine link = new OrderLine(
                        new OrderLine.OrderLineId(),
                        article,
                        cart,
                        orderLine.quantity());

                orderLineRepository.save(link);
            }
        }
        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO quoteToRevisitedCart(Long quoteId) {
        Quote parent = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Quote not found"));
        Customer creator = customerRepository.findById(parent.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Le creator n'existe pas"));
        CartRequestDTO dto = cartPipelineMapper.quoteToCartRevision(parent);
        return this.create(creator, dto);
    }

    @Override
    public CartDTO modify(Long id, CartRequestDTO dto) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" ));

        cart.setReceiverEmail(dto.receiverEmail());

        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartDTO patchStatus(PatchCartStatus dto) {

        Cart cart = cartRepository.findById(dto.crtId())
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " + dto.crtId() + " n'existe pas"));

        CartStatus current = cart.getCrtStatus();
        CartStatus next = dto.crtStatus();

        switch (current) {
            case OPEN -> {
                if (next != CartStatus.VALIDATED) {
                    throw new IllegalStateException(
                            "Impossible de passer un panier OPEN à un autre statut que VALIDATED.");
                }
            }
            case VALIDATED -> {
                if (next != CartStatus.REVISITED) {
                    throw new IllegalStateException(
                            "Impossible de passer un panier VALIDATED à un autre statut que REVISITED.");
                }
            }
            case REVISITED ->
                throw new IllegalStateException(
                        "Un panier REVISITED est figé, il ne peut plus être modifié."
                );
            default -> throw new IllegalStateException(
                    "Statut actuel du panier inconnu : " + current);
        }
        cart.setCrtStatus(next);

        if (next == CartStatus.VALIDATED) {
            publisher.publishEvent(new CartValidatedEvent(cart));
        }
        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional(rollbackOn = ResourceNotFoundException.class)
    public void delete(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" ));

        orderLineRepository.deleteAllByOrdLnId_CartId(cart.getCrtId());
        cartRepository.delete(cart);
    }

}
