package com.mns.cda.saas_facturation.document.Iservice;

import java.util.Map;

/**
 * <p>Sait transformer un template et des données en fichier PDF (en mémoire,
 * sous forme de tableau d'octets).</p>
 *
 * <p>Ne connaît pas Thymeleaf en particulier : c'est {@code ThymeleafPdfGenerator}
 * qui décide comment le template est réellement transformé en PDF. Si on change
 * un jour de moteur de template, seule cette classe-là change.</p>
 */
public interface IPdfGenerator {

    /**
     * <p>Génère un PDF à partir d'un template et des données à afficher dedans.</p>
     *
     * @param templateName nom du fichier de template, sans dossier ni extension
     *                      (ex. {@code "invoice"} pour {@code templates/invoice.html})
     * @param model         les données à injecter dans le template — la clé est le
     *                      nom de variable utilisé dans le template, la valeur est
     *                      la donnée correspondante
     * @return le contenu du PDF, prêt à être stocké ou envoyé au navigateur
     */
    byte[] generatePdf(String templateName, Map<String, Object> model);
}