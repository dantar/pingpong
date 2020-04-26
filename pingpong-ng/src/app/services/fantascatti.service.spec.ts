import { TestBed } from '@angular/core/testing';

import { FantascattiService } from './fantascatti.service';

describe('FantascattiService', () => {
  let service: FantascattiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FantascattiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
