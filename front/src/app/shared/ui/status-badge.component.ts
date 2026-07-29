import { ChangeDetectionStrategy, Component, Input, computed, signal } from '@angular/core';
import { statusMeta } from '../../core/models/document-status';

/** Pastille de statut d'un document commercial. */
@Component({
  selector: 'app-status-badge',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<span [class]="meta().badgeClass">{{ meta().label }}</span>`,
})
export class StatusBadgeComponent {
  private readonly raw = signal<string | null>(null);

  @Input({ required: true })
  set status(value: string | null | undefined) {
    this.raw.set(value ?? null);
  }

  protected readonly meta = computed(() => statusMeta(this.raw()));
}
