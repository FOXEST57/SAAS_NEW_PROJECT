package com.mns.cda.saas_facturation.cart.service;

import com.mns.cda.saas_facturation.cart.DTO.InvoicePdfDTO;
import com.mns.cda.saas_facturation.cart.Iservice.IInvoicePdfService;
import com.mns.cda.saas_facturation.cart.mapper.InvoicePdfMapper;
import com.mns.cda.saas_facturation.cart.model.Invoice;
import com.mns.cda.saas_facturation.document.Iservice.ICompanyService;
import com.mns.cda.saas_facturation.document.Iservice.IDocumentStorageService;
import com.mns.cda.saas_facturation.document.Iservice.IPdfGenerator;
import com.mns.cda.saas_facturation.cart.repository.InvoiceRepository;
import com.mns.cda.saas_facturation.exception.PdfGenerationException;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>Fabrique le PDF d'une facture, puis le range.</p>
 *
 * <p>Cette classe ne fait qu'enchaîner trois collaborateurs, chacun spécialisé :</p>
 *
 * <ol>
 *   <li>{@code InvoicePdfMapper} transforme la facture en données prêtes à afficher</li>
 *   <li>{@code IPdfGenerator} transforme ces données en fichier PDF</li>
 *   <li>{@code IDocumentStorageService} enregistre ce fichier</li>
 * </ol>
 *
 * <p>Elle-même ne sait ni lire les entités, ni dessiner un PDF, ni écrire sur
 * un disque. C'est ce qui permet de changer n'importe laquelle de ces trois
 * étapes — passer du stockage local au cloud, par exemple — sans toucher à
 * cette classe.</p>
 */
@Service
@RequiredArgsConstructor
public class InvoicePdfService implements IInvoicePdfService {

    /** Nom du fichier de template, dans {@code src/main/resources/templates/}. */
    private static final String TEMPLATE = "invoice";

    private final InvoicePdfMapper invoicePdfMapper;
    private final IPdfGenerator pdfGenerator;
    private final IDocumentStorageService storageService;
    private final ICompanyService companyService;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public String generate(Invoice invoice) {

        // Première instruction, et ce n'est pas un hasard : tant qu'on est
        // dans la transaction, on peut encore parcourir les liens entre
        // entités. Une fois cette ligne passée, on travaille sur des données
        // simples et le reste du traitement ne dépend plus de la base.
        InvoicePdfDTO document = invoicePdfMapper.toDTO(invoice);

        // Les deux variables que le template pourra utiliser :
        // « invoice » pour cette facture, « company » pour tes coordonnées.
        Map<String, Object> model = Map.of(
                "invoice", document,
                "company", companyService.getCompany()
        );

        byte[] pdf = pdfGenerator.generatePdf(TEMPLATE, model);

        return storageService.store(pdf, buildKey(invoice));
    }

    /**
     * <p>Va chercher sur le disque le PDF déjà enregistré d'une facture.</p>
     *
     * <p>Cette méthode ne fabrique rien : elle relit le fichier tel qu'il a été
     * créé au moment de l'émission. C'est volontaire — une facture est un
     * document qui a valeur juridique, elle ne doit pas se redessiner
     * différemment parce que le modèle ou tes coordonnées ont changé entre
     * temps.</p>
     *
     * <p>Le déroulé est en trois temps :</p>
     *
     * <ol>
     *   <li>on retrouve la facture en base, pour lire l'endroit où son PDF a
     *       été rangé ({@code invoicePathPDF})</li>
     *   <li>on vérifie que cet endroit est bien renseigné — sans quoi le
     *       fichier n'a jamais été créé, ce qui ne devrait pas arriver</li>
     *   <li>on demande le fichier au service de stockage, qui sait seul où et
     *       comment il est réellement rangé</li>
     * </ol>
     *
     * @param invoiceId identifiant de la facture recherchée
     * @return le fichier PDF, prêt à être renvoyé au navigateur
     * @throws ResourceNotFoundException si aucune facture ne porte cet identifiant
     * @throws PdfGenerationException    si la facture existe mais n'a aucun PDF associé
     */
    @Override
    @Transactional(readOnly = true)
    public Resource retrieve(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non existante"));

        String key = invoice.getInvoicePathPDF();
        if (key == null || key.isBlank()) {
            throw new PdfGenerationException(
                    "La facture " + invoice.getInvoiceNumber() + " n'a pas de PDF enregistré."
            );
        }

        return storageService.retrieve(key);
    }

    /**
     * <p>Construit le nom sous lequel le fichier sera rangé, par exemple
     * {@code invoices/2026/FAC-2026-0007.pdf}.</p>
     *
     * <p>Le classement par année évite de se retrouver avec des dizaines de
     * milliers de fichiers dans un seul dossier au bout de quelques années.</p>
     *
     * <p>Le numéro de facture est nettoyé avant d'être utilisé : il vient de
     * l'extérieur, et un nom de fichier qui contiendrait des {@code ../}
     * permettrait d'écrire ailleurs que dans le dossier prévu. Le stockage se
     * protège déjà de son côté ; on ne compte pas dessus pour autant.</p>
     */
    private String buildKey(Invoice invoice) {
        int year = invoice.getInvoiceCreatedDate().getYear();
        String safeNumber = invoice.getInvoiceNumber().replaceAll("[^A-Za-z0-9._-]", "_");
        return "invoices/" + year + "/" + safeNumber + ".pdf";
    }
}