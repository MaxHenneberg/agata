import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AddGoodsService} from '../add-goods.service';
import {ItemRow} from '../../dataholder/ItemRow';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-modify-pickup',
  templateUrl: './modify-pickup.component.html',
  styleUrls: ['./modify-pickup.component.css']
})
export class ModifyPickupComponent implements OnInit {

  proposal: any;
  proposalId: string;

  addedItems: ItemRow[];


  invoiceForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute, private addGoodsService: AddGoodsService) {
  }

  ngOnInit(): void {
    this.invoiceForm = this.formBuilder.group({
      invoiceId: ['', Validators.required],
    });
    this.proposalId = (this.route.snapshot.paramMap.get('id'));
    console.log(this.proposalId);
    this.addGoodsService.resolveProposalId(this.proposalId).subscribe(res => {
      this.proposal = res;
      console.log(res);
    });
    this.addedItems = [];
  }

  finishModify() {
    this.addGoodsService.finishModfiy(this.proposalId, this.invoiceForm.controls.invoiceId.value, this.addedItems);
  }

  onAddedItem(item: ItemRow) {
    this.addedItems.push(item);
  }

}
