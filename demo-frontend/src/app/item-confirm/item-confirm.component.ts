import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {ItemRow} from '../../dataholder/ItemRow';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-item-confirm',
  templateUrl: './item-confirm.component.html',
  styleUrls: ['./item-confirm.component.css']
})
export class ItemConfirmComponent implements OnInit {

  good: ItemRow;

  @Output()
  itemConfirmed: EventEmitter<ItemRow> = new EventEmitter<ItemRow>();

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.good = this.data.good;
  }

  onItemConfirm() {
    this.itemConfirmed.emit(this.good);
  }

}
