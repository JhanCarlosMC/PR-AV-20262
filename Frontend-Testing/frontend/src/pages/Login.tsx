import { esquemaLogin, type FormularioLogin } from "../schema/login";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { login } from "../services/authServicio";
import type { TokenResponse } from "../models/auth";
import { estadoHttp, mensajeDeError } from "../utils/errores";


export default function Login({ alIngresar }: { alIngresar: (sesion: TokenResponse) => void }) {

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<FormularioLogin>({
    resolver: zodResolver(esquemaLogin),
    mode: 'onTouched',   // valida cada campo al salir de él
    defaultValues: { username: '', password: '' },
  });

  async function alEnviar(datos: FormularioLogin) {
    try {
      const sesion = await login(datos);
      alIngresar(sesion);
    } catch (error) {
      setError('root.servidor', {
        message: estadoHttp(error) === 401
          ? 'Correo o contraseña incorrectos' : mensajeDeError(error)
      })
    }
  }

  return (
    <section className="panel panel-compacto">
      <h1>Ingresar</h1>

      <form onSubmit={handleSubmit(alEnviar)} noValidate>
        <div>
          <label htmlFor="username">Correo electrónico</label>
          <input id="username" type="email" autoComplete="username" {...register('username')} />
          {errors.username && <p className="error">{errors.username.message}</p>}
        </div>

        <div>
          <label htmlFor="password">Contraseña</label>
          <input id="password" type="password" autoComplete="current-password" {...register('password')} />
          {errors.password && <p className="error">{errors.password.message}</p>}
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Ingresando…' : 'Ingresar'}
        </button>

        {errors.root?.servidor && <p className="error" role="alert">{errors.root.servidor.message}</p>}
      </form>
    </section>
  );
}