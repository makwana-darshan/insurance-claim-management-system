export interface Surveyor {
  id: number;
  fullName: string;
  email: string;
}

export interface AssignSurveyorRequest {
  surveyorId: number;
}