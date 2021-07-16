import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {BolTO} from '../../dataholder/BolTO';
import {ContainerTO} from '../../dataholder/ContainerTO';

@Component({
  selector: 'app-accept-container-dialog',
  templateUrl: './accept-container-dialog.component.html',
  styleUrls: ['./accept-container-dialog.component.css']
})
export class AcceptContainerDialogComponent implements OnInit {

  bol: BolTO;
  container: ContainerTO;

  @Output()
  onConfirm: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    console.log(this.data);
    this.container = this.data.container;
    this.bol = this.data.masterBol;
  }

  confirm(state: boolean) {
    console.log(state);
    this.onConfirm.emit(state);
  }

}
