import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RobotAvatarComponent } from './robot-avatar.component';

describe('RobotAvatarComponent', () => {
  let component: RobotAvatarComponent;
  let fixture: ComponentFixture<RobotAvatarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RobotAvatarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RobotAvatarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
