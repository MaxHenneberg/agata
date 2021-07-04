import { TestBed } from '@angular/core/testing';

import { AddGoodsService } from './add-goods.service';

describe('AddGoodsService', () => {
  let service: AddGoodsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddGoodsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
