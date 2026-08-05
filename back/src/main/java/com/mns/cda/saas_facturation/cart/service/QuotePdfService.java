package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.QuotePdfDTO;
import com.mns.cda.saas_facturation.cart.Iservice.IQuotePdfService;
import com.mns.cda.saas_facturation.cart.mapper.QuotePdfMapper;
import com.mns.cda.saas_facturation.cart.model.Quote;
import com.mns.cda.saas_facturation.cart.repository.QuoteRepository;
import com.mns.cda.saas_facturation.document.Iservice.ICompanyService;
import com.mns.cda.saas_facturation.document.Iservice.IDocumentStorageService;
import com.mns.cda.saas_facturation.document.Iservice.IPdfGenerator;
import com.mns.cda.saas_facturation.exception.PdfGenerationException;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>Fabrique le PDF d'un devis, puis le range.</p>
 *
 * <p>Comme son équivalent pour la facture, cette classe ne fait qu'enchaîner
 * trois collaborateurs spécialisés :</p>
 *
 * <ol>
 *   <li>{@code QuotePdfMapper} transforme le devis en données prêtes à afficher</li>
 *   <li>{@code IPdfGenerator} transforme ces données en fichier PDF</li>
 *   <li>{@code IDocumentStorageService} enregistre ce fichier</li>
 * </ol>
 *
 * <p>Elle-même ne sait ni lire les entités, ni dessiner un PDF, ni écrire sur
 * un disque — ce qui permet de changer n'importe laquelle de ces trois étapes
 * sans y toucher.</p>
 */
@Service
@RequiredArgsConstructor
public class QuotePdfService implements IQuotePdfService {

    /** Nom du fichier de template, dans {@code src/main/resources/templates/}. */
    private static final String TEMPLATE = "quote";

    private final QuotePdfMapper quotePdfMapper;
    private final QuoteRepository quoteRepository;
    private final IPdfGenerator pdfGenerator;
    private final IDocumentStorageService storageService;
    private final ICompanyService companyService;

    @Override
    @Transactional(readOnly = true)
    public String generate(Quote quote) {

        // Première instruction, et ce n'est pas un hasard : tant qu'on est
        // dans la transaction, on peut encore parcourir les liens entre
        // entités. Une fois cette ligne passée, on travaille sur des données
        // simples et le reste du traitement ne dépend plus de la base.
        QuotePdfDTO document = quotePdfMapper.toDTO(quote);

        // Les deux variables que le template pourra utiliser :
        // « quote » pour ce devis, « company » pour tes coordonnées.
        Map<String, Object> model = Map.of(
                "quote", document,
                "company", companyService.getCompany()
        );

        byte[] pdf = pdfGenerator.generatePdf(TEMPLATE, model);

        return storageService.store(pdf, buildKey(quote));
    }

    /**
     * <p>Va chercher sur le disque le PDF déjà enregistré d'un devis.</p>
     *
     * <p>Le déroulé est en trois temps :</p>
     *
     * <ol>
     *   <li>on retrouve le devis en base, pour lire l'endroit où son PDF a été
     *       rangé ({@code qotPathPDF})</li>
     *   <li>on vérifie que cet endroit est bien renseigné — s'il est vide,
     *       c'est que le devis n'a jamais été transmis, donc qu'aucun PDF
     *       n'existe</li>
     *   <li>on demande le fichier au service de stockage, qui sait seul où et
     *       comment il est réellement rangé</li>
     * </ol>
     *
     * @throws ResourceNotFoundException si aucun devis ne porte cet identifiant
     * @throws PdfGenerationException    si le devis existe mais n'a pas encore été transmis
     */
    @Override
    @Transactional(readOnly = true)
    public Resource retrieve(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non existant"));

        String key = quote.getQotPathPDF();
        if (key == null || key.isBlank()) {
            throw new PdfGenerationException(
                    "Le devis " + quote.getQotNumber()
                            + " n'a pas encore de PDF : il n'a pas été transmis au client."
            );
        }

        return storageService.retrieve(key);
    }

    /**
     * <p>Construit le nom sous lequel le fichier sera rangé, par exemple
     * {@code quotes/2026/DEV-2026-0007.pdf}.</p>
     *
     * <p>Le classement par année évite de se retrouver avec des dizaines de
     * milliers de fichiers dans un seul dossier au bout de quelques années.</p>
     *
     * <p>Les révisions ne s'écrasent pas entre elles : leur numéro porte un
     * indice ({@code DEV-2026-0007-B}), donc leur fichier aussi. Chaque
     * version transmise au client reste consultable.</p>
     *
     * <p>Le numéro est nettoyé avant d'être utilisé : il vient de l'extérieur,
     * et un nom de fichier qui contiendrait des {@code ../} permettrait
     * d'écrire ailleurs que dans le dossier prévu. Le stockage se protège déjà
     * de son côté ; on ne compte pas dessus pour autant.</p>
     */
    private String buildKey(Quote quote) {
        int year = quote.getQotCreatedDate().getYear();
        String safeNumber = quote.getQotNumber().replaceAll("[^A-Za-z0-9._-]", "_");
        return "quotes/" + year + "/" + safeNumber + ".pdf";
    }
}