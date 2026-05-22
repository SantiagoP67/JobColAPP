import React from 'react';
import { Briefcase, ArrowRight, Search, FileText, Handshake, Wrench, Zap, PaintBucket, Hammer, Truck, Leaf, ShieldCheck, Star, MapPin, Users, Building2, Phone, Mail, CheckCircle2, TrendingUp, Clock } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Button from '../components/Button';
import CreateProfileModal from '../components/CreateProfileModal';
import AuthModal from '../components/AuthModal';
import { useToast } from '../context/ToastContext';
import './LandingPage.css';
import VerifyCodeModal from '../components/VerifyCodeModal';
import EmployerProfileModal from '../components/EmployerProfileModal';
import ForgotPasswordModal from '../components/ForgotPasswordModal';
import { useSearchParams } from 'react-router-dom';

const STEPS = [
  {
    icon: <Search size={28} />, 
    title: "Busca oportunidades",
    description: "Explora miles de ofertas laborales filtradas por categoría, ubicación y rango salarial.",
    color: "#4f46e5"
  },
  {
    icon: <FileText size={28} />,
    title: "Postúlate fácilmente",
    description: "Aplica con un solo clic y completa una evaluación adaptativa impulsada por IA.",
    color: "#7c3aed"
  },
  {
    icon: <Handshake size={28} />,
    title: "Conecta y trabaja",
    description: "Recibe tu contrato digital, trabaja con confianza y construye tu reputación.",
    color: "#a855f7"
  }
];

const CATEGORIES = [
  { icon: <Wrench size={26} />, name: "Plomería", count: "1.2K+", gradient: "linear-gradient(135deg, #3b82f6, #1d4ed8)" },
  { icon: <Zap size={26} />, name: "Electricidad", count: "980+", gradient: "linear-gradient(135deg, #f59e0b, #d97706)" },
  { icon: <Hammer size={26} />, name: "Construcción", count: "2.1K+", gradient: "linear-gradient(135deg, #ef4444, #dc2626)" },
  { icon: <PaintBucket size={26} />, name: "Pintura", count: "750+", gradient: "linear-gradient(135deg, #8b5cf6, #7c3aed)" },
  { icon: <Truck size={26} />, name: "Domicilios", count: "3.4K+", gradient: "linear-gradient(135deg, #10b981, #059669)" },
  { icon: <Leaf size={26} />, name: "Jardinería", count: "620+", gradient: "linear-gradient(135deg, #22c55e, #16a34a)" },
];

const TESTIMONIALS = [
  {
    name: "Carlos Mendoza",
    role: "Electricista",
    location: "Bogotá",
    rating: 5,
    text: "Gracias a JobCol encontré trabajo en menos de 2 días. La plataforma es muy fácil de usar y los pagos son puntuales.",
    avatar: "CM"
  },
  {
    name: "María González",
    role: "Empleadora",
    location: "Medellín",
    rating: 5,
    text: "Contraté un plomero excelente en cuestión de horas. El sistema de calificaciones me dio mucha confianza.",
    avatar: "MG"
  },
  {
    name: "Juan Rodríguez",
    role: "Pintor",
    location: "Cali",
    rating: 4,
    text: "Llevo 6 meses usando JobCol y he completado más de 30 servicios. Mi mejor decisión profesional.",
    avatar: "JR"
  }
];

const FEATURES = [
  { icon: <ShieldCheck size={22} />, title: "Contratos digitales", desc: "Formaliza cada servicio con un contrato seguro y transparente." },
  { icon: <Star size={22} />, title: "Sistema de reseñas", desc: "Construye tu reputación con calificaciones verificadas de cada trabajo." },
  { icon: <TrendingUp size={22} />, title: "Evaluación con IA", desc: "Demuestra tus habilidades con nuestro test adaptativo inteligente." },
  { icon: <Clock size={22} />, title: "Pagos puntuales", desc: "Recibe tu pago al finalizar cada servicio, sin retrasos." },
];

export default function LandingPage() {
  const [pendingEmail, setPendingEmail] = React.useState(null);
  const [isVerifyOpen, setIsVerifyOpen] = React.useState(false);
  const [isAuthOpen, setIsAuthOpen] = React.useState(false);
  const [authTab, setAuthTab] = React.useState('login');
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [isProfileOpen, setIsProfileOpen] = React.useState(false);
  const [isEmployerProfileOpen, setIsEmployerProfileOpen] = React.useState(false);
  const [isForgotPasswordOpen, setIsForgotPasswordOpen] = React.useState(false);
  const [searchParams] = useSearchParams();
  const redirect = searchParams.get("redirect");
  const openAuth = (tab) => {
    setAuthTab(tab);
    setIsAuthOpen(true);
  };
  return (
    <div className="landing-page">
      <div className="header-wrapper">
        <header className="header container">
          <div className="logo cursor-pointer" onClick={() => navigate('/')}>
            <div className="logo-icon">
              <Briefcase size={22} color="white" />
            </div>
            <span className="logo-text">JobCol</span>
          </div>
          <nav className="header-nav">
            <a href="#how-it-works" className="header-nav-link">Cómo funciona</a>
            <a href="#categories" className="header-nav-link">Categorías</a>
            <a href="#testimonials" className="header-nav-link">Testimonios</a>
          </nav>
          <div className="header-actions">
            <button className="header-btn-login" onClick={() => openAuth('login')}>
              Iniciar Sesión
            </button>
            <button className="header-btn-register" onClick={() => openAuth('register')}>
              Crear Cuenta
              <ArrowRight size={16} />
            </button>
          </div>
        </header>
      </div>

      {/* HERO */}
      <main className="hero-wrapper">
        <div className="hero-bg-gradient"></div>
        <div className="container hero-container">
          <div className="hero-section">
            <div className="hero-badge animate-fade-in">
              <ShieldCheck size={15} />
              <span>Plataforma #1 de trabajo informal en Colombia</span>
            </div>
            <h1 className="hero-title animate-fade-in">
              Conecta con trabajos
              <br />
              <span className="text-gradient">por horas y servicios</span>
            </h1>
            
            <p className="hero-subtitle animate-fade-in delay-100">
              La plataforma de trabajo informal más completa de Colombia. Encuentra turnos, 
              jornadas y servicios, o contrata profesionales para tus necesidades del día a día.
            </p>

            <div className="hero-actions animate-fade-in delay-200">
              <button className="hero-btn-primary" onClick={() => openAuth('register')}>
                Comenzar gratis
                <ArrowRight size={18} />
              </button>
              <button className="hero-btn-secondary" onClick={() => {
                document.getElementById('how-it-works')?.scrollIntoView({ behavior: 'smooth' });
              }}>
                Ver cómo funciona
              </button>
            </div>

            <div className="hero-social-proof animate-fade-in delay-300">
              <div className="social-avatars">
                <div className="social-avatar" style={{ background: '#4f46e5' }}>C</div>
                <div className="social-avatar" style={{ background: '#7c3aed' }}>M</div>
                <div className="social-avatar" style={{ background: '#a855f7' }}>J</div>
                <div className="social-avatar" style={{ background: '#6366f1' }}>A</div>
              </div>
              <div className="social-text">
                <span className="social-highlight">+50.000</span> trabajadores confían en JobCol
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* STATS */}
      <section className="stats-strip">
        <div className="container">
          <div className="stats-grid-landing">
            <div className="stat-item-landing">
              <h2 className="stat-number-landing">10K+</h2>
              <p className="stat-label-landing">Trabajos activos</p>
            </div>
            <div className="stat-divider"></div>
            <div className="stat-item-landing">
              <h2 className="stat-number-landing">50K+</h2>
              <p className="stat-label-landing">Trabajadores</p>
            </div>
            <div className="stat-divider"></div>
            <div className="stat-item-landing">
              <h2 className="stat-number-landing">2K+</h2>
              <p className="stat-label-landing">Empresas</p>
            </div>
            <div className="stat-divider"></div>
            <div className="stat-item-landing">
              <h2 className="stat-number-landing">4.8<Star size={20} fill="#fbbf24" color="#fbbf24" style={{ verticalAlign: 'middle', marginLeft: 2 }} /></h2>
              <p className="stat-label-landing">Calificación promedio</p>
            </div>
          </div>
        </div>
      </section>

      {/* HOW IT WORKS */}
      <section className="section-light" id="how-it-works">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Proceso simple</span>
            <h2 className="section-title">¿Cómo funciona?</h2>
            <p className="section-subtitle">Tres pasos simples para comenzar a trabajar o contratar</p>
          </div>

          <div className="steps-grid">
            {STEPS.map((step, index) => (
              <div key={index} className="step-card">
                <div className="step-connector" />
                <div className="step-number">{index + 1}</div>
                <div className="step-icon-wrapper" style={{ background: `${step.color}12`, color: step.color }}>
                  {step.icon}
                </div>
                <h3 className="step-title">{step.title}</h3>
                <p className="step-description">{step.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FEATURES */}
      <section className="section-white">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Ventajas</span>
            <h2 className="section-title">¿Por qué elegir JobCol?</h2>
            <p className="section-subtitle">Todo lo que necesitas para trabajar con confianza y seguridad</p>
          </div>

          <div className="features-grid">
            {FEATURES.map((feat, i) => (
              <div key={i} className="feature-card">
                <div className="feature-icon">{feat.icon}</div>
                <h3 className="feature-title">{feat.title}</h3>
                <p className="feature-desc">{feat.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CATEGORIES */}
      <section className="section-light" id="categories">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Explora</span>
            <h2 className="section-title">Categorías populares</h2>
            <p className="section-subtitle">Encuentra profesionales en las áreas más demandadas</p>
          </div>

          <div className="categories-grid">
            {CATEGORIES.map((cat, index) => (
              <div key={index} className="category-card" onClick={() => openAuth('register')}>
                <div className="category-icon" style={{ background: cat.gradient }}>{cat.icon}</div>
                <h3 className="category-name">{cat.name}</h3>
                <span className="category-count">{cat.count} ofertas</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* TESTIMONIALS */}
      <section className="section-white" id="testimonials">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Testimonios</span>
            <h2 className="section-title">Lo que dicen nuestros usuarios</h2>
            <p className="section-subtitle">Historias reales de personas que confían en JobCol</p>
          </div>

          <div className="testimonials-grid">
            {TESTIMONIALS.map((testimonial, index) => (
              <div key={index} className="testimonial-card">
                <div className="testimonial-quote">"</div>
                <div className="testimonial-stars">
                  {Array.from({ length: testimonial.rating }, (_, i) => (
                    <Star key={i} size={15} fill="#fbbf24" color="#fbbf24" />
                  ))}
                </div>
                <p className="testimonial-text">{testimonial.text}</p>
                <div className="testimonial-author">
                  <div className="testimonial-avatar">{testimonial.avatar}</div>
                  <div>
                    <h4 className="testimonial-name">{testimonial.name}</h4>
                    <p className="testimonial-role">
                      {testimonial.role} · <MapPin size={11} /> {testimonial.location}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="cta-section">
        <div className="container">
          <div className="cta-content">
            <div className="cta-glow"></div>
            <h2 className="cta-title">¿Listo para comenzar?</h2>
            <p className="cta-subtitle">
              Únete a miles de trabajadores y empleadores que ya confían en JobCol
            </p>
            <div className="cta-actions">
              <button className="cta-btn" onClick={() => openAuth('register')}>
                Crear cuenta gratis <ArrowRight size={18} />
              </button>
            </div>
            <p className="cta-note">Sin costo · Sin tarjeta de crédito · Comienza en 2 minutos</p>
          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="landing-footer">
        <div className="container">
          <div className="footer-grid">
            <div className="footer-brand">
              <div className="logo">
                <div className="logo-icon">
                  <Briefcase size={18} color="white" />
                </div>
                <span className="logo-text footer-logo-text">JobCol</span>
              </div>
              <p className="footer-description">
                La plataforma líder de trabajo informal en Colombia. Conectando talento con oportunidades desde 2026.
              </p>
            </div>

            <div className="footer-links">
              <h4>Plataforma</h4>
              <ul>
                <li><a href="#how-it-works">Cómo funciona</a></li>
                <li><a href="#" onClick={(e) => { e.preventDefault(); openAuth('register'); }}>Registrarse</a></li>
                <li><a href="#" onClick={(e) => { e.preventDefault(); openAuth('login'); }}>Iniciar sesión</a></li>
              </ul>
            </div>

            <div className="footer-links">
              <h4>Categorías</h4>
              <ul>
                <li><a href="#">Plomería</a></li>
                <li><a href="#">Electricidad</a></li>
                <li><a href="#">Construcción</a></li>
                <li><a href="#">Domicilios</a></li>
              </ul>
            </div>

            <div className="footer-links">
              <h4>Contacto</h4>
              <ul>
                <li className="footer-contact-item"><Mail size={14} /> soporte@jobcol.co</li>
                <li className="footer-contact-item"><Phone size={14} /> +57 300 123 4567</li>
                <li className="footer-contact-item"><MapPin size={14} /> Bogotá, Colombia</li>
              </ul>
            </div>
          </div>

          <div className="footer-bottom">
            <p>© 2026 JobCol. Todos los derechos reservados.</p>
          </div>
        </div>
      </footer>

      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        initialTab={authTab}
        onRegisterSuccess={() => {
          setIsAuthOpen(false);
          const role = localStorage.getItem("role");
          if (role === "EMPLEADOR") {
            setIsEmployerProfileOpen(true);
          } else {
            setIsProfileOpen(true);
          }
        }}
        onRequireVerifyCode={({ username, email }) => {
          setIsAuthOpen(false);
          setPendingEmail(email || username); 
          setIsVerifyOpen(true);
        }}
        onForgotPassword={() => setIsForgotPasswordOpen(true)}
      />

      <CreateProfileModal isOpen={isProfileOpen} onClose={() => setIsProfileOpen(false)} />

      <VerifyCodeModal
        isOpen={isVerifyOpen}
        email={pendingEmail}
        onClose={() => setIsVerifyOpen(false)}
        onVerified={() => {
          setIsVerifyOpen(false);
          navigate(redirect || '/dashboard');
        }}
      />

      <EmployerProfileModal isOpen={isEmployerProfileOpen} onClose={() => setIsEmployerProfileOpen(false)} />

      <ForgotPasswordModal
        isOpen={isForgotPasswordOpen}
        onClose={() => setIsForgotPasswordOpen(false)}
        onSuccess={() => {
          setIsForgotPasswordOpen(false);
          openAuth('login');
        }}
      />
    </div>
  );
}
