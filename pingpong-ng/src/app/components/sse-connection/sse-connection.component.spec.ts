import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SseConnectionComponent } from './sse-connection.component';

describe('SseConnectionComponent', () => {
  let component: SseConnectionComponent;
  let fixture: ComponentFixture<SseConnectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SseConnectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SseConnectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
