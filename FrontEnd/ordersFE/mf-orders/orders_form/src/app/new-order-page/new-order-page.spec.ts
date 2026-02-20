import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NewOrderPage } from './new-order-page';

describe('NewOrderPage', () => {
  let component: NewOrderPage;
  let fixture: ComponentFixture<NewOrderPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewOrderPage],
    }).compileComponents();

    fixture = TestBed.createComponent(NewOrderPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
