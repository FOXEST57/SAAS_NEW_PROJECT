package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.model.Invoice;
import org.springframework.core.io.Resource;

/**
 * <p>Sait fabriquer le PDF d'une facture et le ranger.</p>
 *
 * <p>Une interface dédiée à la facture, et non une interface commune à tous
 * les documents : un devis et une facture n'ont ni les mêmes données, ni les
 * mêmes mentions légales, ni le même moment de génération. Les regrouper de
 * force créerait plus de contraintes que de gains.</p>
 */
public interface IInvoicePdfService {

    /**
     * <p>Fabrique le PDF de cette facture et l'enregistre.</p>
     *
     * <p>À appeler au moment où la facture est émise, pas plus tard : le PDF
     * doit figer le document tel qu'il est <b>à cet instant</b>. Si on le
     * générait seulement au premier téléchargement, un changement de logo, de
     * mentions légales ou de coordonnées entre-temps modifierait un document
     * qui a pourtant déjà valeur juridique.</p>
     *
     * @return la clé de stockage du fichier créé, à conserver dans
     *         {@code invoice.invoicePathPDF} pour pouvoir le relire plus tard
     */
    String generate(Invoice invoice);

    /**
     * <p>Relit le PDF déjà enregistré d'une facture.</p>
     *
     * <p>Ne régénère rien : le fichier renvoyé est exactement celui qui a été
     * fabriqué à l'émission. C'est la raison d'être de la génération immédiate
     * — le client et toi devez pouvoir relire le même document des années plus
     * tard, quoi qu'il soit arrivé au modèle ou aux coordonnées depuis.</p>
     *
     * @param invoiceId identifiant de la facture
     * @return le fichier, prêt à être envoyé au navigateur
     */
    Resource retrieve(Long invoiceId);
}