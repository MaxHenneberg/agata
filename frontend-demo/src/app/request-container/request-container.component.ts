import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-request-container',
  templateUrl: './request-container.component.html',
  styleUrls: ['./request-container.component.css']
})
export class RequestContainerComponent implements OnInit {

  requestForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.requestForm = this.formBuilder.group({
      shippingLine: [null, Validators.required],
      portOfLoading: [null, Validators.required],
      portOfDischarge: [null, Validators.required],
      forwardingAgentNo: [null, Validators.required],
      requestedType: [null, Validators.required],
      lclDestination: [null, Validators.required],
    });
  }

  submit(): void {

  }

}
