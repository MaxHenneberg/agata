import { TestBed } from '@angular/core/testing';

import { BolCombinerService } from './bol-combiner.service';

describe('BolCombinerService', () => {
  let service: BolCombinerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BolCombinerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
