import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AddGoodsService} from '../add-goods.service';
import {ItemRow} from '../../dataholder/ItemRow';

@Component({
  selector: 'app-modify-pickup',
  templateUrl: './modify-pickup.component.html',
  styleUrls: ['./modify-pickup.component.css']
})
export class ModifyPickupComponent implements OnInit {

  proposal: {
    buyer: string;
    supplier: string;
    lclCompany: string;
    invoiceId: string;
  };
  proposalId: string;

  addedItems: ItemRow[];

  constructor(private route: ActivatedRoute, private addGoodsService: AddGoodsService) {
  }

  ngOnInit(): void {
    this.proposalId = (this.route.snapshot.paramMap.get('id'));
    this.proposal = this.addGoodsService.resolveProposalId(this.proposalId);
    this.addedItems = [];
  }

  finishModify() {
    this.addGoodsService.finishModfiy(this.proposalId, this.addedItems);
  }

  onAddedItem(item: ItemRow) {
    this.addedItems.push(item);
  }

}
