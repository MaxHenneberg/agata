import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {ItemRow} from '../../dataholder/ItemRow';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ScannerDialogComponent} from '../scanner-dialog/scanner-dialog.component';
import {AddGoodsService} from '../add-goods.service';
import {ItemConfirmComponent} from '../item-confirm/item-confirm.component';
import {MatTable} from '@angular/material/table';

@Component({
  selector: 'app-add-goods',
  templateUrl: './add-goods.component.html',
  styleUrls: ['./add-goods.component.css']
})
export class AddGoodsComponent implements OnInit {

  @ViewChild(MatTable) table: MatTable<any>;

  columnsToDisplay = ['mark', 'id', '#packages', 'product'];

  addedItems: ItemRow[];

  @Output()
  onAddedItem: EventEmitter<ItemRow> = new EventEmitter<ItemRow>();

  scanDialogRef: MatDialogRef<ScannerDialogComponent>;
  itemDialogRef: MatDialogRef<ItemConfirmComponent>;

  constructor(private dialog: MatDialog, private addGoodsService: AddGoodsService) {
  }

  ngOnInit(): void {
    this.addedItems = [];
  }

  openDialog() {
    this.scanDialogRef = this.dialog.open(ScannerDialogComponent);

    this.scanDialogRef.componentInstance.onSuccessfulScan.subscribe((event) => this.onSuccessfulScan(event));
  }

  onSuccessfulScan(event: string) {
    console.log(event);
    this.scanDialogRef.close();
    const addedGood = this.addGoodsService.resolveGoodsId(event);
    console.log(addedGood);
    this.itemDialogRef = this.dialog.open(ItemConfirmComponent, {
      data: {good: addedGood}
    });
    this.itemDialogRef.componentInstance.itemConfirmed.subscribe((item) => {
      this.addedItems.push(item);
      this.onAddedItem.emit(item);
      this.table.renderRows();
      this.itemDialogRef.close();
    });
  }

}
