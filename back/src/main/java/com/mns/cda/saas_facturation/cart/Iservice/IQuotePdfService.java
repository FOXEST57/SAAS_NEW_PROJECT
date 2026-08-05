package com.mns.cda.saas_facturation.cart.Iservice;

import com.mns.cda.saas_facturation.cart.model.Quote;
import org.springframework.core.io.Resource;

/**
 * <p>Sait fabriquer le PDF d'un devis, le ranger, et le relire.</p>
 *
 * <p>Une interface dédiée au devis, distincte de celle de la facture : les
 * deux documents n'ont ni les mêmes données, ni les mêmes mentions légales,
 * ni le même moment de génération. Les regrouper de force créerait plus de
 * contraintes que de gains.</p>
 */
public interface IQuotePdfService {

    /**
     * <p>Fabrique le PDF de ce devis et l'enregistre.</p>
     *
     * <p>À appeler au moment où le devis est transmis au client (passage en
     * {@code PENDING}), et non à sa création. Tant qu'il est en
     * {@code CREATED}, c'est un brouillon librement modifiable : figer un
     * document qui bouge encore n'aurait pas de sens. Dès qu'il part chez le
     * client, en revanche, il doit cesser de changer.</p>
     *
     * @return la clé de stockage du fichier créé, à conserver dans
     *         {@code quote.qotPathPDF} pour pouvoir le relire plus tard
     */
    String generate(Quote quote);

    /**
     * <p>Relit le PDF déjà enregistré d'un devis.</p>
     *
     * <p>Ne fabrique rien : le fichier renvoyé est exactement celui qui a été
     * transmis au client. Si le catalogue, tes tarifs ou tes coordonnées ont
     * changé depuis, ce document-là n'en sait rien — et c'est bien l'objectif,
     * puisque le client détient le même.</p>
     *
     * @param quoteId identifiant du devis
     * @return le fichier, prêt à être renvoyé au navigateur
     */
    Resource retrieve(Long quoteId);
}