import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClaimTimeline } from './claim-timeline';

describe('ClaimTimeline', () => {
  let component: ClaimTimeline;
  let fixture: ComponentFixture<ClaimTimeline>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClaimTimeline],
    }).compileComponents();

    fixture = TestBed.createComponent(ClaimTimeline);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
