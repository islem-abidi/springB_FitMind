import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DatePipe } from '@angular/common';
import { FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private baseUrl = 'http://localhost:8081/Evenement/api/events'; // Pour les événements
  private inscriptionUrl = 'http://localhost:8081/Evenement/InscriptionEvenement'; // Pour les inscriptions

  list: any[] = [];
  choixmenu: string = 'A';
  public formData!: FormGroup;

  constructor(private http: HttpClient, private datePipe: DatePipe) {}

  // ✅ ÉVÉNEMENTS

  getAll(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  getData(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  createData(event: any, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('event', JSON.stringify(event));
    if (file) {
      formData.append('file', file);
    }
    return this.http.post(this.baseUrl, formData);
  }

  updatedata(info: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${info.idEvenement}`, info);
  }

  deleteData(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }

  // ✅ INSCRIPTIONS

  addInscription(inscription: any): Observable<any> {
    return this.http.post(`${this.inscriptionUrl}/addInscription`, inscription);
  }

  removeInscription(userId: number, eventId: number): Observable<any> {
    return this.http.delete(`${this.inscriptionUrl}/deleteByUserAndEvent/${userId}/${eventId}`);
  }

  getInscriptionsByEvent(eventId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.inscriptionUrl}/event/${eventId}`);
  }

  getAllInscriptions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.inscriptionUrl}/inscriptions`);
  }
  updateInscriptionStatus(inscriptionId: number, newStatus: string): Observable<any> {
    return this.http.put(`${this.inscriptionUrl}/status/${inscriptionId}`, { 
      statutInscription: newStatus 
    });
    
    
  }
  generateQRCode(inscriptionId: number): Observable<any> {
    return this.http.post(`${this.inscriptionUrl}/generateQRCode/${inscriptionId}`, {});
  }

  // Télécharger le QR code
  downloadQRCode(inscriptionId: number): Observable<Blob> {
    return this.http.get(`${this.inscriptionUrl}/downloadQRCode/${inscriptionId}`, {
      responseType: 'blob'
    });
  }

  // Télécharger le billet PDF
  downloadTicket(inscriptionId: number): Observable<Blob> {
    return this.http.get(`${this.inscriptionUrl}/downloadTicket/${inscriptionId}`, {
      responseType: 'blob'
    });
  }

  // Vérifier si une inscription peut être annulée
  canCancelInscription(inscriptionId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.inscriptionUrl}/canCancel/${inscriptionId}`);
  }

  // Trouver l'ID d'inscription à partir du user ID et de l'event ID
  getInscriptionIdByUserAndEvent(userId: number, eventId: number): Observable<number> {
    return this.http.get<number>(`${this.inscriptionUrl}/findInscriptionId/${userId}/${eventId}`);
  }
  } 
