package com.mns.cda.saas_facturation.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Sert l'application Angular compilée depuis {@code src/main/resources/static}.
 *
 * <p>Sans cette configuration, seule la page d'accueil fonctionnerait. Angular
 * utilise l'historique HTML5 : l'adresse {@code /documents/3} est gérée
 * <em>dans le navigateur</em>, elle ne correspond à aucun fichier. Un accès
 * direct à cette adresse — un signet, un rafraîchissement, un lien collé —
 * arrive pourtant bien jusqu'à Spring, qui répond 404 faute de ressource.</p>
 *
 * <p>La règle ci-dessous renvoie {@code index.html} pour toute adresse qui ne
 * désigne ni un fichier réel ni une route de l'API : Angular lit alors l'URL et
 * affiche le bon écran.</p>
 *
 * <p><b>L'ordre de résolution rend l'API prioritaire.</b> Spring interroge
 * d'abord les contrôleurs ({@code RequestMappingHandlerMapping}), et seulement
 * ensuite les ressources statiques. {@code /article} atteint donc bien
 * {@code ArticleController} ; seules les adresses qu'aucun contrôleur ne
 * revendique parviennent ici.</p>
 */
@Configuration
public class SpaConfig implements WebMvcConfigurer {

    /**
     * Préfixes à ne jamais rediriger vers {@code index.html}.
     *
     * <p>Un appel d'API inconnu doit répondre 404, pas renvoyer une page HTML :
     * un client qui attend du JSON et reçoit du HTML produit une erreur bien
     * plus difficile à diagnostiquer qu'un franc 404.</p>
     */
    private static final String[] API_PREFIXES = {
            "v3/api-docs", "swagger-ui", "actuator"
    };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {

                    @Override
                    protected Resource getResource(String resourcePath, Resource location)
                            throws IOException {

                        Resource requested = location.createRelative(resourcePath);

                        // Fichier réellement présent : on le sert tel quel.
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }

                        // Documentation et supervision gardent leur propre 404.
                        for (String prefix : API_PREFIXES) {
                            if (resourcePath.startsWith(prefix)) {
                                return null;
                            }
                        }

                        // Route Angular : on laisse l'application décider.
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
