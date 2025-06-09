import { apiClient } from './api';
import { AuthResponse } from '../models';

class DebugService {
  private readonly baseUrl = '/debug';

  /**
   * Debug login by account name
   * @param accountName The account name to login with
   * @returns LoginResponse with account details
   */
  async loginByName(accountName: string): Promise<AuthResponse> {
    console.log('=== DebugService.loginByName START ===');
    console.log('Account name:', accountName);
    
    try {
      const response = await apiClient.get<AuthResponse>(`${this.baseUrl}/login-by-name/${accountName}`);
      console.log('Debug login successful:', response);
      console.log('=== DebugService.loginByName END ===');
      return response;
    } catch (error) {
      console.error('=== ERROR in DebugService.loginByName ===');
      console.error('Account name:', accountName);
      console.error('Error:', error);
      console.error('=== END ERROR ===');
      throw error;
    }
  }
}

export const debugService = new DebugService();
export default debugService;
