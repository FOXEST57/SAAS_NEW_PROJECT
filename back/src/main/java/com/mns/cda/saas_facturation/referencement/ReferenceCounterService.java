package com.mns.cda.saas_facturation.referencement;

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
                String ref = type.name() + "-" +
                        String.format("%06d", counter.getCounter());
                yield ref;
            }
            case CART -> {
                String ref = corporation.getCorpPreRefCart() +
                        "-" + type.name() + "-" +
                        String.format("%06d", counter.getCounter());
                yield ref;
            }
            case QUOTE -> {
                String ref = corporation.getCorpPreRefQuote() +
                        "-" + type.name() + "-" +
                        String.format("%06d", counter.getCounter());
                yield ref;
            }
            case INVOICE -> {
                String ref = corporation.getCorpPreRefInvoice() +
                        "-" + type.name() + "-" +
                        String.format("%06d", counter.getCounter());
                yield ref;
            }
            case COMMAND -> {
                String ref = type.name() + "-" +
                        String.format("%06d", counter.getCounter());
                yield ref;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);

        };

    }
}
