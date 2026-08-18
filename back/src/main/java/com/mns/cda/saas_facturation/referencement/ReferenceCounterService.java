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
    public String generateReference(Corporation corporation , ReferenceType type) {
        if (corporation == null && type != ReferenceType.ARTICLE && type != ReferenceType.COMMAND) {
            throw new ResourceNotFoundException("l'entreprise demandé n'existe pas");
        }

        ReferenceCounter counter = counterRepository.findByCorporation_CorpIdAndObjectType(corporation.getCorpId(), type)
                .orElseGet(() ->{
                    ReferenceCounter rc = new ReferenceCounter();
                    rc.setCorporation(corporation);
                    rc.setObjectType(type);
                    rc.setCounter(0L);
                    return counterRepository.save(rc);
                });

        counter.setCounter(counter.getCounter() + 1);
        counterRepository.save(counter);
        return switch (type) {
            case ARTICLE -> {
                yield type.name() + "-" +
                        String.format("%06d", counter.getCounter());
            }
            case CART -> {
                yield corporation.getCorpPreRefCart() +
                        "-" + type.name() + "-" +
                        String.format("%06d", counter.getCounter());
            }
            case QUOTE -> {
                yield corporation.getCorpPreRefQuote() +
                        "-" + type.name() + "-" +
                        String.format("%06d", counter.getCounter());
            }
            case INVOICE -> {
                yield corporation.getCorpPreRefInvoice() +
                        "-" + type.name() + "-" +
                        String.format("%06d", counter.getCounter());
            }
            case COMMAND -> {
                yield type.name() + "-" +
                        String.format("%06d", counter.getCounter());
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

    }
}
