import { RoleResponse } from './../model/RoleResponse';
import { Injectable } from '@angular/core';
import { Role } from '../model/Role';
import { of, Observable, from } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { RoleGetResponse } from '../model/RoleGetResponse';

const urlEndPoint = "http://localhost:8080/api/microappservice/role";

@Injectable()
export class RoleService {

  private headerAuth = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': 'Bearer eyJhbGciOiJIUzM4NCJ9.eyJhdXRob3JpdGllcyI6Ilt7XCJhdXRob3JpdHlcIjpcIlJPTEVfQURNSU5cIn1dIiwic3ViIjoiUk9PVCIsImlhdCI6MTU3MjI5NDE0NiwiZXhwIjoxNTcyMjk3NzQ2fQ.uDE35RTACMB-DVl953yJe3VIP3L9beClrV8_oNWsLOFmf7OpJjdzyDLkYLpHKYH_'
  });
  lRoles: Role[];

  constructor(private http: HttpClient) { }

  getRoles(): Observable<RoleGetResponse> {
    return this.http.get<RoleGetResponse>
      (urlEndPoint,
        {
          headers: this.headerAuth,
          responseType: 'json',
          withCredentials: true
        }
      );
  }

  postRole(role: Role): Observable<RoleResponse> {
    return this.http.post<RoleResponse>
      (urlEndPoint, role,
        {
          headers: this.headerAuth,
          responseType: 'json',
          withCredentials: true
        }
      );
  }

  putRole(role: Role): Observable<RoleResponse> {
    return this.http.put<RoleResponse>
      (urlEndPoint, role,
        {
          headers: this.headerAuth,
          responseType: 'json',
          withCredentials: true
        }
      );
  }

  deleteRole(role: Role): Observable<RoleResponse> {
    return this.http.delete<RoleResponse>
      (urlEndPoint + '/' + role.idRole,
        {
          headers: this.headerAuth,
          responseType: 'json',
          withCredentials: true
        }
      );
  }

}
