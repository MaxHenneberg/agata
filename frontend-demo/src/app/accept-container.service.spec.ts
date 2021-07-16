import { TestBed } from '@angular/core/testing';

import { AcceptContainerService } from './accept-container.service';

describe('AcceptContainerService', () => {
  let service: AcceptContainerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AcceptContainerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
