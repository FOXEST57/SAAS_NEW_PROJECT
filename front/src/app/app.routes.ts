import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell.component';

/**
 * Toutes les vues sont chargées à la demande (lazy loading) afin de garder
 * un bundle initial léger.
 */
export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'tableau-de-bord' },

      {
        path: 'tableau-de-bord',
        title: 'Tableau de bord — Klimafact',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },

      /* ---- Documents commerciaux ---- */
      {
        path: 'documents',
        title: 'Devis & factures — Klimafact',
        loadComponent: () =>
          import('./features/carts/cart-list.component').then((m) => m.CartListComponent),
      },
      {
        path: 'documents/nouveau',
        title: 'Nouveau document — Klimafact',
        loadComponent: () =>
          import('./features/carts/cart-editor.component').then((m) => m.CartEditorComponent),
      },
      {
        path: 'documents/:id',
        title: 'Document — Klimafact',
        loadComponent: () =>
          import('./features/carts/cart-editor.component').then((m) => m.CartEditorComponent),
      },
      {
        path: 'documents/:id/impression',
        title: 'Impression — Klimafact',
        loadComponent: () =>
          import('./features/carts/document-print.component').then(
            (m) => m.DocumentPrintComponent,
          ),
      },

      /* ---- Catalogue ---- */
      {
        path: 'articles',
        title: 'Articles — Klimafact',
        loadComponent: () =>
          import('./features/articles/article-list.component').then((m) => m.ArticleListComponent),
      },
      {
        path: 'categories',
        title: 'Catégories — Klimafact',
        loadComponent: () =>
          import('./features/categories/category-list.component').then(
            (m) => m.CategoryListComponent,
          ),
      },
      {
        path: 'tva',
        title: 'Taux de TVA — Klimafact',
        loadComponent: () => import('./features/tva/tva-list.component').then((m) => m.TvaListComponent),
      },
      {
        path: 'references',
        title: 'Références fournisseurs & fabricants — Klimafact',
        loadComponent: () =>
          import('./features/references/reference-list.component').then(
            (m) => m.ReferenceListComponent,
          ),
      },

      /* ---- Tiers ---- */
      {
        path: 'clients',
        title: 'Clients — Klimafact',
        loadComponent: () =>
          import('./features/customers/customer-list.component').then(
            (m) => m.CustomerListComponent,
          ),
      },
      {
        path: 'types-de-compte',
        title: 'Types de compte — Klimafact',
        loadComponent: () =>
          import('./features/account-types/account-type-list.component').then(
            (m) => m.AccountTypeListComponent,
          ),
      },
      {
        path: 'fournisseurs',
        title: 'Fournisseurs — Klimafact',
        loadComponent: () =>
          import('./features/suppliers/supplier-list.component').then(
            (m) => m.SupplierListComponent,
          ),
      },
      {
        path: 'fabricants',
        title: 'Fabricants — Klimafact',
        loadComponent: () =>
          import('./features/makers/maker-list.component').then((m) => m.MakerListComponent),
      },

      /* ---- Référentiel géographique ---- */
      {
        path: 'adresses',
        title: 'Adresses — Klimafact',
        loadComponent: () =>
          import('./features/addresses/address-list.component').then((m) => m.AddressListComponent),
      },
      {
        path: 'villes',
        title: 'Villes — Klimafact',
        loadComponent: () =>
          import('./features/geo/city-list.component').then((m) => m.CityListComponent),
      },
      {
        path: 'codes-postaux',
        title: 'Codes postaux — Klimafact',
        loadComponent: () =>
          import('./features/geo/postal-code-list.component').then((m) => m.PostalCodeListComponent),
      },
      {
        path: 'pays',
        title: 'Pays — Klimafact',
        loadComponent: () =>
          import('./features/geo/country-list.component').then((m) => m.CountryListComponent),
      },
      {
        path: 'associations-cp-ville',
        title: 'Associations code postal / ville — Klimafact',
        loadComponent: () =>
          import('./features/geo/postal-code-city-list.component').then(
            (m) => m.PostalCodeCityListComponent,
          ),
      },

      {
        path: '**',
        loadComponent: () =>
          import('./features/dashboard/not-found.component').then((m) => m.NotFoundComponent),
      },
    ],
  },
];
