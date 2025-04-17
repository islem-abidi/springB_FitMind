import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TemplateComponent } from './template/template.component';
import { FormsModule } from '@angular/forms';
import { AbonnementsComponent } from './components/abonnements/abonnements.component';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';   
import { NavbarfrontComponent } from './components/navbarfront/navbarfront.component';
import { FooterComponent } from './components/back/footer/footer.component';
import { NavbarComponent } from './components/back/navbar/navbar.component';
import { SidebarComponent } from './components/back/sidebar/sidebar.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { AdminLayoutComponent } from './backoff/layouts/admin-layout/admin-layout.component';
import { AuthLayoutComponent } from './backoff/layouts/auth-layout/auth-layout.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ClipboardModule } from 'ngx-clipboard';
import { BacktempComponent } from './components/backtemp/backtemp.component';
import { AbonnementsbackComponent } from './components/abonnementsback/abonnementsback.component';
import { ListEventComponent } from './components/Evenementsback/list-event-back/list-event.component';
import { AddEventComponent } from './components/Evenementsback/add-event-back/add-event.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { NgxPaginationModule } from 'ngx-pagination';
import { DatePipe } from '@angular/common';
import { EvenementUserComponent } from './components/Evenementsfront/evenement-user/evenement-user.component';           
@NgModule({
  declarations: [
    AppComponent,
    TemplateComponent,
    AbonnementsComponent,
    NavbarfrontComponent,
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    AdminLayoutComponent,
    AuthLayoutComponent,
    BacktempComponent,
    AbonnementsbackComponent,
    ListEventComponent ,
    AddEventComponent,
    EvenementUserComponent




],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    RouterModule,
    BrowserAnimationsModule,
    NgbModule,
    ClipboardModule,
    MatToolbarModule,
    FormsModule,              
    ReactiveFormsModule ,
    ToastrModule.forRoot(),
    MatToolbarModule,
    MatIconModule,
    MatDialogModule,
    NgxPaginationModule
     

    
  ],
  providers: [
    DatePipe // ✅ Injection correcte ici
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }