import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RequestSlotComponent } from './request-slot/request-slot.component';
import { AppRoutingModule } from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatCardModule} from '@angular/material/card';
import { ApproveSlotComponent } from './approve-slot/approve-slot.component';
import { RequestContainerComponent } from './request-container/request-container.component';
import { AddGoodsComponent } from './add-goods/add-goods.component';
import {ZXingScannerModule} from '@zxing/ngx-scanner';
import { ScannerDialogComponent } from './scanner-dialog/scanner-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import { ItemConfirmComponent } from './item-confirm/item-confirm.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import { ModifyPickupComponent } from './modify-pickup/modify-pickup.component';
import {MatDividerModule} from '@angular/material/divider';
import {MatTableModule} from '@angular/material/table';
import { BolCombinerComponent } from './bol-combiner/bol-combiner.component';
import { AcceptContainerComponent } from './accept-container/accept-container.component';
import { AcceptContainerDialogComponent } from './accept-container-dialog/accept-container-dialog.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { ContainerFromShippinglineComponent } from './container-from-shippingline/container-from-shippingline.component';
import { GoodsFromLclcompanyComponent } from './goods-from-lclcompany/goods-from-lclcompany.component';
import { BolViewComponent } from './bol-view/bol-view.component';
import {HttpClientModule} from '@angular/common/http';
import { TrackingStateComponent } from './tracking-state/tracking-state.component';
import {MatStepperModule} from '@angular/material/stepper';
import { CustomStepperComponent } from './custom-stepper/custom-stepper.component';
import {environment} from '../environments/environment';
// import { SuccessDialogComponent } from './success-dialog/success-dialog.component';
// import { LogoPlaygroundComponent } from './logo-playground/logo-playground.component';

@NgModule({
  declarations: [
    AppComponent,
    RequestSlotComponent,
    ApproveSlotComponent,
    RequestContainerComponent,
    AddGoodsComponent,
    ScannerDialogComponent,
    ItemConfirmComponent,
    ModifyPickupComponent,
    BolCombinerComponent,
    AcceptContainerComponent,
    AcceptContainerDialogComponent,
    ContainerFromShippinglineComponent,
    GoodsFromLclcompanyComponent,
    BolViewComponent,
    TrackingStateComponent,
    CustomStepperComponent,
    // SuccessDialogComponent,
    // LogoPlaygroundComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatExpansionModule,
    MatCardModule,
    ZXingScannerModule,
    MatDialogModule,
    MatToolbarModule,
    MatIconModule,
    MatSidenavModule,
    MatDividerModule,
    MatTableModule,
    MatSnackBarModule,
    HttpClientModule,
    MatStepperModule
  ],
  providers: [
    {provide: 'BACKEND_API_URL', useValue: environment.apiUrl},
    {provide: 'PROFILE', useValue: environment.profile}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
