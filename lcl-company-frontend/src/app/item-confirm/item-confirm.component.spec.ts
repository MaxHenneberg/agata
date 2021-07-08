import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemConfirmComponent } from './item-confirm.component';

describe('ItemConfirmComponent', () => {
  let component: ItemConfirmComponent;
  let fixture: ComponentFixture<ItemConfirmComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ItemConfirmComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemConfirmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
