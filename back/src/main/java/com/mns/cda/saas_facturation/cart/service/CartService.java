package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchCartStatus;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.QuoteRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.ICartService;
import com.mns.cda.saas_facturation.cart.mapper.CartPipelineMapper;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import com.mns.cda.saas_facturation.cart.service.pipeline.CartValidatedEvent;
import com.mns.cda.saas_facturation.enumeration.CartStatus;
import com.mns.cda.saas_facturation.enumeration.QuoteStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.cart.mapper.CartMapper;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.cart.model.Cart;
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
    public CartDTO create(CartRequestDTO dto) {

        Customer customer = customerRepository.findById(dto.ctmId())
                .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'id " +dto.ctmId()+ " n'existe pas" ));

        Quote quote = quoteRepository.findById(dto.parentQuoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Le devis avec l'id " +dto.parentQuoteId()+ " n'existe pas" ));

        Cart cart = new Cart();
        cart.setCrtRef(dto.crtRef());
        cart.setCrtStatus(CartStatus.OPEN);
        cart.setCustomer(customer);
        cart.setParentQuoteId(quote.getQotId());

        cartRepository.save(cart);

        if (dto.orderLines() != null && !dto.orderLines().isEmpty()) {
            for (OrderLineRequestDTO orderLine : dto.orderLines()) {

                Article article = articleRepository.findById(orderLine.articleId())
                        .orElseThrow(() -> new ResourceNotFoundException("L'article avec l'id " + orderLine.articleId() + " n'existe pas"));

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
        Quote parent = quoteRepository.findById(quoteId).orElseThrow(() -> new RuntimeException("Quote not found"));
        CartRequestDTO dto = cartPipelineMapper.quoteToCartRevision(parent);
        return this.create(dto);
    }

    @Override
    public CartDTO modify(Long id, CartRequestDTO dto) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " +id+ " n'existe pas" ));

        Customer customer = customerRepository.findById(dto.ctmId())
                .orElseThrow(() -> new ResourceNotFoundException("Le client avec l'id " +dto.ctmId()+ " n'existe pas" ));

        cart.setCrtRef(dto.crtRef());
        cart.setCustomer(customer);

        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartDTO patchStatus(PatchCartStatus dto) {
        Cart cart = cartRepository.findById(dto.crtId())
                .orElseThrow(() -> new ResourceNotFoundException("Le panier avec l'id " + dto.crtId() + " n'existe pas"));
        cart.setCrtStatus(dto.crtStatus());
        if (cart.getCrtStatus() == CartStatus.VALIDATED) {
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
