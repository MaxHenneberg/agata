import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ItemRow} from '../../dataholder/ItemRow';
import {RequestSlotService} from '../request-slot.service';

@Component({
  selector: 'app-request-slot',
  templateUrl: './request-slot.component.html',
  styleUrls: ['./request-slot.component.css']
})
export class RequestSlotComponent implements OnInit {

  requestForm: FormGroup;
  itemForm: FormGroup;

  itemRows: ItemRow[];

  constructor(private formBuilder: FormBuilder, private requestSlotService: RequestSlotService) {
  }

  ngOnInit(): void {
    this.requestForm = this.formBuilder.group({
      buyer: [null, Validators.required],
      supplier: [null, Validators.required],
      arrivalParty: [null, Validators.required],
      depStreet: [null, Validators.required],
      depCity: [null, Validators.required],
      depState: [null, Validators.required],
      depPostalCode: [null, Validators.required],
      depCountry: [null, Validators.required],
      arrStreet: [null, Validators.required],
      arrCity: [null, Validators.required],
      arrState: [null, Validators.required],
      arrPostalCode: [null, Validators.required],
      arrCountry: [null, Validators.required],
      expectedGoods: [[], Validators.required]
    });

    this.itemForm = this.formBuilder.group({
      mark: [null],
      identityNumber: [null],
      noOfPackages: [null],
      descProduct: [null],
      descTypeOfPackage: [null],
      descQuantity: [null],
      netWeight: [null],
      grossWeight: [null],
      measurement: [[]]
    });

    this.itemRows = [] as ItemRow[];
  }

  addItem() {
    this.requestForm.controls.expectedGoods.value.push({
      mark: this.itemForm.controls.mark.value,
      identityNumber: this.itemForm.controls.identityNumber.value,
      noOfPackages: this.itemForm.controls.noOfPackages.value,
      descriptionOfGoods: {
        product: this.itemForm.controls.descProduct.value,
        typeOfPackage: this.itemForm.controls.descTypeOfPackage.value,
        quantity: this.itemForm.controls.descQuantity.value
      },
      netWeight: this.itemForm.controls.netWeight.value,
      grossWeight: this.itemForm.controls.grossWeight.value,
      measurement: this.itemForm.controls.measurement.value
    } as ItemRow);
    // this.requestForm.controls.expectedGoods.setValue(this.itemRows);
    this.itemForm.reset();
  }

  submit() {
    if (this.requestForm.valid) {
      console.log(this.requestForm.value);
      this.requestSlotService.requestSlot({
        arrivalAddress: {
          street: this.requestForm.controls.arrStreet.value,
          city: this.requestForm.controls.arrCity.value,
          country: this.requestForm.controls.arrCountry.value,
          postalCode: this.requestForm.controls.arrPostalCode.value,
          state: this.requestForm.controls.arrState.value
        },
        arrivalParty: this.requestForm.controls.arrivalParty.value,
        departureAddress: {
          street: this.requestForm.controls.depStreet.value,
          city: this.requestForm.controls.depCity.value,
          country: this.requestForm.controls.depCountry.value,
          postalCode: this.requestForm.controls.depPostalCode.value,
          state: this.requestForm.controls.depState.value
        },
        expectedGoods: this.requestForm.controls.expectedGoods.value,
        supplier: this.requestForm.controls.supplier.value,
        buyer: this.requestForm.controls.buyer.value
      });
    }
  }

}
