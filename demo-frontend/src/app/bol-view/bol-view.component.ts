import {Component, Input, OnInit} from '@angular/core';
import {BolTO} from '../../dataholder/BolTO';

@Component({
  selector: 'app-bol-view',
  templateUrl: './bol-view.component.html',
  styleUrls: ['./bol-view.component.css']
})
export class BolViewComponent implements OnInit {

  @Input()
  bol: BolTO;

  constructor() {
  }

  ngOnInit(): void {
  }

}
