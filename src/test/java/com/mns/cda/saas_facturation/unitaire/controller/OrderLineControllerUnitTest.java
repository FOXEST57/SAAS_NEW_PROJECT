package com.mns.cda.saas_facturation.unitaire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.cda.saas_facturation.DTO.ArticleLightDTO;
import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.CartResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateOrderLineDTO;
import com.mns.cda.saas_facturation.Iservice.IOrderLineService;
import com.mns.cda.saas_facturation.controller.OrderLineController;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.model.OrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = OrderLineController.class)
public class OrderLineControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOrderLineService orderLineService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderLineDTO orderLineDTO;
    private OrderLineRequestDTO orderLineRequestDTO;
    private UpdateOrderLineDTO updateOrderLineDTO;
    private OrderLine.OrderLineId orderLineId;
    private ArticleLightDTO article;
    private CartResponseDTO cart;
    private CartDTO cartDTO;


    @BeforeEach
    void setUp() {
        orderLineId = new OrderLine.OrderLineId(1L,1L);

        article = new ArticleLightDTO(
                1L,
                "Ref-001",
                "souris",
                "c'est une souris",
                1,
                BigDecimal.ONE
        );

        cart = new CartResponseDTO(
                1L,
                "Cart-001",
                "Valid",
                List.of()
        );

        cartDTO = new CartDTO(
                1L,
                "Cart-001",
                null,
                null,
                "Valid",
                null,
                List.of()
        );

        orderLineDTO = new OrderLineDTO(
                orderLineId,
                1,
                article,
                cart
        );

        orderLineRequestDTO = new OrderLineRequestDTO(
                1L,
                1L,
                1
        );

        updateOrderLineDTO = new UpdateOrderLineDTO(
                1
        );



    }

    // ------------------------------------------------------------------
    // GET /order-line/list
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /order-line/list-articles/{cartId} -> 200 et la liste des lignes de commande")
    void getOrderLineListArticle_devraitRetourner200() throws Exception {
        when(orderLineService.findByCartId(1L)).thenReturn(List.of(orderLineDTO));


        mockMvc.perform(get("/order-line/list-articles/{cartId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cart.crtId").value(1))
                .andExpect(jsonPath("$[0].article.artId").value(1));

        verify(orderLineService, times(1)).findByCartId(1L);
    }
    @Test
    @DisplayName("GET /order-line/list-articles/{cartId} -> 404")
    void getOrderLineListArticlesNotExist_devraitRetourner404() throws Exception {
        when(orderLineService.findByCartId(99L)).thenThrow(new ResourceNotFoundException("OrderLine not found"));

        mockMvc.perform(get("/order-line/list-articles/{cartId}", 99L))
                .andExpect(status().isNotFound());

        verify(orderLineService, times(1)).findByCartId(99L);
    }

    @Test
    @DisplayName("GET /order-line/list-cart/{articleId} -> 200 et la liste des lignes de commande")
    void getOrderLineListCart_devraitRetourner200() throws Exception {
        when(orderLineService.findByArtId(1L)).thenReturn(List.of(cartDTO));


        mockMvc.perform(get("/order-line/list-cart/{articleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].crtId").value(1))
                .andExpect(jsonPath("$[0].crtRef").value("Cart-001"));

        verify(orderLineService, times(1)).findByArtId(1L);
    }
    @Test
    @DisplayName("GET /order-line/list-cart/{articleId} -> 404")
    void getOrderLineListCartNotExist_devraitRetourner404() throws Exception {
        when(orderLineService.findByArtId(99L)).thenThrow(new ResourceNotFoundException("OrderLine not found"));

        mockMvc.perform(get("/order-line/list-cart/{articleId}", 99L))
                .andExpect(status().isNotFound());

        verify(orderLineService, times(1)).findByArtId(99L);
    }

    // ------------------------------------------------------------------
    // GET /orderLine/{artId}/{mkrId}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("GET /order-line/{artId}/{mkrId} -> 200 quand la reference constructeur existe")
    void getOrderLine_devraitRetourner200() throws Exception {
        when(orderLineService.findById(1L,1L)).thenReturn(orderLineDTO);

        mockMvc.perform(get("/order-line/{artId}/{mkrId}",1L,1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cart.crtId").value(1))
                .andExpect(jsonPath("$.article.artId").value(1));
        verify(orderLineService, times(1)).findById(1L,1L);
    }

    @Test
    @DisplayName("GET /orderLine/{artId}/{mkrId} -> 404 quand la reference constructeur n'existe pas")
    void getOrderLineById_quandIntrouvable_devraitRetourner404() throws Exception {
        when(orderLineService.findById(99L, 99L)).thenThrow(new ResourceNotFoundException("OrderLine not found"));

        mockMvc.perform(get("/order-line/{artId}/{mkrId}", 99L, 99L))
                .andExpect(status().isNotFound());

        verify(orderLineService, times(1)).findById(99L, 99L);
    }

    // ------------------------------------------------------------------
    // POST /orderLine
    // ------------------------------------------------------------------

    @Test
    @DisplayName("POST /order-line -> 201 avec un corps valide")
    void createOrderLine_avecDonneesValides_devraitRetourner201EtLaLigneDeCommandeCree() throws Exception {
        // any(...) car seul le comportement du service nous intéresse ici,
        // pas la valeur exacte de l'argument (déjà couverte par le test de validation ci-dessous)
        when(orderLineService.create(any(OrderLineRequestDTO.class))).thenReturn(orderLineDTO);

        mockMvc.perform(post("/order-line")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Convertit l'objet Java orderLineRequestDTO en JSON pour le body de la requête
                        .content(objectMapper.writeValueAsString(orderLineRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cart.crtId").value(1))
                .andExpect(jsonPath("$.article.artId").value(1));

        verify(orderLineService, times(1)).create(any(OrderLineRequestDTO.class));
    }


    // ------------------------------------------------------------------
    // PUT /order-line/{mkrId}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("PUT /order-line/{artId}/{mkrId} -> 200 avec la reference constructeur modifiée")
    void updateOrderLine_devraitRetourner200EtLaLigneDeCommandeModifiee() throws Exception {
        when(orderLineService.update(eq(1L), eq(1L), any(UpdateOrderLineDTO.class))).thenReturn(orderLineDTO);

        mockMvc.perform(put("/order-line/{artId}/{mkrId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateOrderLineDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cart.crtId").value(1))
                .andExpect(jsonPath("$.article.artId").value(1));

        verify(orderLineService, times(1)).update(eq(1L), eq(1L), any(UpdateOrderLineDTO.class));
    }

    // ------------------------------------------------------------------
    // DELETE /order-line/{mkrId}
    // ------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /order-line/{artId}/{mkrId} -> 204 sans corps")
    void deleteOrderLine_devraitRetourner204() throws Exception {
        // Le service ne retourne rien (void) : on utilise doNothing() plutôt que when()
        // (when() ne fonctionne qu'avec des méthodes qui retournent une valeur).
        org.mockito.Mockito.doNothing().when(orderLineService).delete(1L, 1L);

        mockMvc.perform(delete("/order-line/{artId}/{mkrId}", 1L, 1L))
                .andExpect(status().isNoContent());

        verify(orderLineService, times(1)).delete(1L, 1L);
    }
}
