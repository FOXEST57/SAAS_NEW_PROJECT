package com.mns.cda.saas_facturation.referencement;

import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.user.model.Corporation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceCounterService {

    private final ReferenceCounterRepository counterRepository;

    @Transactional
    public String generateReference(Corporation corporation, ReferenceType type) {

        // Corporation obligatoire pour certains types
        boolean corpRequired =
                type == ReferenceType.CART ||
                        type == ReferenceType.QUOTE ||
                        type == ReferenceType.INVOICE;

        if (corporation == null && corpRequired) {
            throw new ResourceNotFoundException("L'entreprise demandée n'existe pas");
        }

        // Récupération du compteur selon corporation null ou non
        ReferenceCounter counter;

        if (corporation == null) {
            // ARTICLE & COMMAND → corporation facultative
            counter = counterRepository.findByCorporationIsNullAndObjectType(type)
                    .orElseGet(() -> {
                        ReferenceCounter rc = new ReferenceCounter();
                        rc.setCorporation(null);
                        rc.setObjectType(type);
                        rc.setCounter(0L);
                        return counterRepository.save(rc);
                    });
        } else {
            // CART, QUOTE, INVOICE → corporation obligatoire
            counter = counterRepository.findByCorporation_CorpIdAndObjectType(corporation.getCorpId(), type)
                    .orElseGet(() -> {
                        ReferenceCounter rc = new ReferenceCounter();
                        rc.setCorporation(corporation);
                        rc.setObjectType(type);
                        rc.setCounter(0L);
                        return counterRepository.save(rc);
                    });
        }

        // Incrémentation
        counter.setCounter(counter.getCounter() + 1);
        counterRepository.save(counter);

        // Génération de la référence
        return switch (type) {
            case ARTICLE -> {
                yield type.name() + "-" + String.format("%06d", counter.getCounter());
            }
            case COMMAND -> {
                yield type.name() + "-" + String.format("%06d", counter.getCounter());
            }
            case CART -> {
                String prefixe = corporation.getCorpPreRefCart();
                if (prefixe == null) {
                    yield type.name() + "-" + String.format("%06d", counter.getCounter());
                } else {
                    yield corporation.getCorpPreRefCart()
                            + "-" + type.name()
                            + "-" + String.format("%06d", counter.getCounter());
                }
            }
            case QUOTE -> {
                String prefixe = corporation.getCorpPreRefCart();
                if (prefixe == null) {
                    yield type.name() + "-" + String.format("%06d", counter.getCounter());
                } else {
                    yield corporation.getCorpPreRefQuote()
                            + "-" + type.name()
                            + "-" + String.format("%06d", counter.getCounter());
                }
            }
            case INVOICE -> {
                String prefixe = corporation.getCorpPreRefCart();
                if (prefixe == null) {
                    yield type.name() + "-" + String.format("%06d", counter.getCounter());
                } else {
                    yield corporation.getCorpPreRefInvoice()
                            + "-" + type.name()
                            + "-" + String.format("%06d", counter.getCounter());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}

