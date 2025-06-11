import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Employee, RegisterRequest, UpdateEmployeeRequest } from '../../models';

interface EmployeeFormProps {
  initialData?: Employee;
  onSubmit: (data: RegisterRequest | UpdateEmployeeRequest) => void;
  isAdding: boolean;
}

type EmployeeFormData = {
  accountName: string;
  email: string;
  phoneNumber: string;
  employeeType: number;
  citizenId?: string;
  password?: string;
};

export const EmployeeForm = ({ initialData, onSubmit, isAdding }: EmployeeFormProps) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm<EmployeeFormData>({
    defaultValues: isAdding
      ? {
          accountName: '',
          email: '',
          phoneNumber: '',
          employeeType: 1,
          citizenId: '',
          password: ''
        }
      : {
          accountName: initialData?.accountName || '',
          email: initialData?.email || '',
          phoneNumber: initialData?.phoneNumber || '',
          employeeType: initialData?.employeeType || 1
        }
  });

  // If initialData changes (editing another employee), update form values
  useEffect(() => {
    if (!isAdding && initialData) {
      reset({
        accountName: initialData.accountName || '',
        email: initialData.email || '',
        phoneNumber: initialData.phoneNumber || '',
        employeeType: initialData.employeeType || 1
      });
    }
    if (isAdding) {
      reset({
        accountName: '',
        email: '',
        phoneNumber: '',
        employeeType: 1,
        citizenId: '',
        password: ''
      });
    }
  }, [initialData, isAdding, reset]);

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="form-control">
        <label className="label">Tên tài khoản</label>
        <input
          {...register('accountName', { required: 'Tên tài khoản là bắt buộc' })}
          className="input input-bordered"
        />
        {errors.accountName && (
          <span className="text-red-500 text-sm">{errors.accountName.message}</span>
        )}
      </div>

      <div className="form-control">
        <label className="label">Email</label>
        <input
          type="email"
          {...register('email', {
            required: 'Email là bắt buộc',
            pattern: { value: /^\S+@\S+$/, message: 'Định dạng email không hợp lệ' }
          })}
          className="input input-bordered"
        />
        {errors.email && (
          <span className="text-red-500 text-sm">{errors.email.message}</span>
        )}
      </div>

      <div className="form-control">
        <label className="label">Số điện thoại</label>
        <input
          type="text"
          {...register('phoneNumber', {
            required: 'Số điện thoại là bắt buộc',
            pattern: { value: /^[0-9-+]+$/, message: 'Định dạng số điện thoại không hợp lệ' }
          })}
          className="input input-bordered"
        />
        {errors.phoneNumber && (
          <span className="text-red-500 text-sm">{errors.phoneNumber.message}</span>
        )}
      </div>

      <div className="form-control">
        <label className="label">Loại nhân viên</label>
        <select
          {...register('employeeType', { required: 'Loại nhân viên là bắt buộc', valueAsNumber: true })}
          className="select select-bordered"
        >
          <option value={1}>Tiếp nhận lịch bay</option>
          <option value={2}>Bán vé</option>
          <option value={3}>Dịch vụ khách hàng</option>
          <option value={4}>Kế toán</option>
          <option value={5}>Quản trị hệ thống</option>
        </select>
        {errors.employeeType && (
          <span className="text-red-500 text-sm">{errors.employeeType.message}</span>
        )}
      </div>

      {isAdding && (
        <>
          <div className="form-control">
            <label className="label">Căn cước công dân</label>
            <input
              {...register('citizenId', {
                required: 'Căn cước công dân là bắt buộc',
                pattern: { value: /^[0-9]+$/, message: 'Định dạng căn cước công dân không hợp lệ' }
              })}
              className="input input-bordered"
            />
            {errors.citizenId && (
              <span className="text-red-500 text-sm">{errors.citizenId.message}</span>
            )}
          </div>
          <div className="form-control">
            <label className="label">Mật khẩu</label>
            <input
              type="password"
              {...register('password', {
                required: 'Mật khẩu là bắt buộc',
                minLength: { value: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự' }
              })}
              className="input input-bordered"
            />
            {errors.password && (
              <span className="text-red-500 text-sm">{errors.password.message}</span>
            )}
          </div>
        </>
      )}

      <button type="submit" className="btn btn-primary w-full">
        {isAdding ? 'Thêm nhân viên' : 'Cập nhật nhân viên'}
      </button>
    </form>
  );
};
