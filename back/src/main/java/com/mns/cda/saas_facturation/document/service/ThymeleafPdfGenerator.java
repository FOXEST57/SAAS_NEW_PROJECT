package com.mns.cda.saas_facturation.document.service;

import com.mns.cda.saas_facturation.document.Iservice.IPdfGenerator;
import com.mns.cda.saas_facturation.exception.PdfGenerationException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * <p>Implémentation d'{@link IPdfGenerator} : elle sait transformer un template
 * et des données en fichier PDF.</p>
 *
 * <p>Ça se passe en deux étapes, chacune dans sa propre méthode ci-dessous :</p>
 * <ol>
 *   <li>Thymeleaf transforme le template + les données en texte HTML.</li>
 *   <li>openhtmltopdf transforme ce texte HTML en PDF.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class ThymeleafPdfGenerator implements IPdfGenerator {

    // Spring crée et fournit automatiquement ce TemplateEngine — pas besoin de
    // l'instancier soi-même, dès que la dépendance Thymeleaf est dans le pom.xml.
    private final TemplateEngine templateEngine;

    /**
     * <p>Méthode publique appelée de l'extérieur (par exemple par un futur
     * {@code InvoicePdfService}). Elle enchaîne juste les deux étapes.</p>
     */
    @Override
    public byte[] generatePdf(String templateName, Map<String, Object> model) {
        String html = renderHtml(templateName, model);
        return convertToPdf(html);
    }

    /**
     * <p><b>Étape 1.</b> Va chercher le fichier de template (par exemple
     * {@code templates/invoice.html}) et remplace les emplacements réservés
     * par les vraies données, pour obtenir une chaîne de caractères HTML.</p>
     *
     * <p>{@code model} contient ces données. Par exemple, si {@code model}
     * contient la clé {@code "invoice"}, le template peut afficher
     * {@code th:text="${invoice.invoiceNumber}"} pour récupérer la valeur.</p>
     */
    private String renderHtml(String templateName, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(templateName, context);
    }

    /**
     * <p><b>Étape 2.</b> Prend le texte HTML produit à l'étape 1 et le
     * transforme en un vrai fichier PDF, gardé en mémoire sous forme de
     * tableau d'octets ({@code byte[]}) plutôt qu'écrit sur le disque —
     * l'écriture sur le disque, ce sera le travail de
     * {@code IDocumentStorageService}, pas celui de cette classe.</p>
     *
     * <p>{@code ByteArrayOutputStream} sert de "récipient" en mémoire dans
     * lequel openhtmltopdf écrit le PDF au fur et à mesure qu'il le construit.</p>
     *
     * @throws PdfGenerationException si le HTML est mal formé ou si la
     *                                conversion échoue pour une autre raison
     */
    private byte[] convertToPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerationException("Impossible de générer le PDF", e);
        }
    }
}