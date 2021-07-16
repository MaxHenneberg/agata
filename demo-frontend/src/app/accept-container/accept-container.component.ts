import {Component, OnInit} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {ScannerDialogComponent} from '../scanner-dialog/scanner-dialog.component';
import {AcceptContainerService} from '../accept-container.service';
import {AcceptContainerDialogComponent} from '../accept-container-dialog/accept-container-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-accept-container',
  templateUrl: './accept-container.component.html',
  styleUrls: ['./accept-container.component.css']
})
export class AcceptContainerComponent implements OnInit {

  scanDialogRef: MatDialogRef<ScannerDialogComponent>;
  acceptContainerDialogRef: MatDialogRef<AcceptContainerDialogComponent>;

  constructor(private dialog: MatDialog, private acceptContainerService: AcceptContainerService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
  }

  openDialog() {
    this.scanDialogRef = this.dialog.open(ScannerDialogComponent);
    this.scanDialogRef.componentInstance.onSuccessfulScan.subscribe((event) => this.onSuccessfulScan(event));
  }

  onSuccessfulScan(container: string) {
    this.scanDialogRef.close();
    this.acceptContainerService.resolveContainerToMasterBol(container).subscribe((result) => {
      this.acceptContainerDialogRef = this.dialog.open(AcceptContainerDialogComponent, {
        data: {
          container: result.masterBol.containerInformationList[0],
          masterBol: result.masterBol
        }
      });
      this.acceptContainerDialogRef.componentInstance.onConfirm.subscribe((bool) => {
        if (bool) {
          this.snackBar.open('Accepted Container: ' + container);
          this.acceptContainerService.acceptContainer(container);
        } else {
          this.snackBar.open('Declined Container: ' + container);
        }
        this.acceptContainerDialogRef.close();
      });
    });
  }

}
