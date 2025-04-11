import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddForumComponent } from './add-forum.component';

describe('AddForumComponent', () => {
  let component: AddForumComponent;
  let fixture: ComponentFixture<AddForumComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddForumComponent]
    });
    fixture = TestBed.createComponent(AddForumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
