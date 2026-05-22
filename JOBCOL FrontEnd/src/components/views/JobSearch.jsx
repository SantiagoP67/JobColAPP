import React, { useState, useEffect } from 'react';
import { Search, Bookmark, Loader2, XCircle } from 'lucide-react';
import Button from '../Button';
import JobCard from '../JobCard';
import { useToast } from '../../context/ToastContext';
import JobDetailsModal from '../JobDetailsModal';
import SavedJobsModal from '../SavedJobsModal';
import TestModal from '../TestModal';
import TestResultModal from '../TestResultModal';

import { getAllOffers } from '../../services/offerService';
import { createPostulation, getPostulationsByWorker } from '../../services/postulationService';
import { getCurrentUser } from '../../services/authService';
import { getReviewsByUser } from '../../services/reviewService';

const CATEGORIES = [
  'Todas las categorías', 'Plomería', 'Electricidad', 'Construcción', 'Aseo',
  'Pintura', 'Jardinería', 'Carpintería', 'Domicilios', 'Mecánica', 'Otro'
];

const TYPES = [
  'Todos los tipos', 'Por Servicio', 'Por Hora', 'Por Jornada', 'Fijo'
];

const LOCATIONS = [
  'Todas las ubicaciones', 'Bogotá', 'Medellín', 'Cali', 'Barranquilla', 'Cartagena',
  'Bucaramanga', 'Pereira', 'Manizales', 'Santa Marta', 'Ibagué',
  'Cúcuta', 'Villavicencio', 'Pasto', 'Montería', 'Neiva'
];

const SALARY_RANGES = [
  { label: 'Todos los pagos', min: 0, max: Infinity },
  { label: 'Menos de $100.000', min: 0, max: 100000 },
  { label: '$100.000 - $300.000', min: 100000, max: 300000 },
  { label: '$300.000 - $500.000', min: 300000, max: 500000 },
  { label: 'Más de $500.000', min: 500000, max: Infinity },
];

export default function JobSearch() {
  const { showToast } = useToast();

  const [jobs, setJobs] = useState([]);
  const [user, setUser] = useState(null);
  const [savedOpen, setSavedOpen] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);

  // Filters
  const [searchText, setSearchText] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('Todas las categorías');
  const [selectedType, setSelectedType] = useState('Todos los tipos');
  const [selectedLocation, setSelectedLocation] = useState('Todas las ubicaciones');
  const [selectedSalaryRange, setSelectedSalaryRange] = useState(0);

  // Test flow
  const [testOpen, setTestOpen] = useState(false);
  const [testJob, setTestJob] = useState(null);
  const [testResult, setTestResult] = useState(null);
  const [testResultOpen, setTestResultOpen] = useState(false);

  // Prevent double-click & loading
  const [applying, setApplying] = useState(false);
  const [loadingTest, setLoadingTest] = useState(false);

  // Track applied offers
  const [appliedOfferIds, setAppliedOfferIds] = useState(new Set());

  // Employer ratings cache
  const [employerRatings, setEmployerRatings] = useState({});

  // Saved jobs
  const [savedJobs, setSavedJobs] = useState(() => {
    try { return JSON.parse(localStorage.getItem('savedJobs') || '[]'); }
    catch { return []; }
  });

  useEffect(() => {
    fetchJobs();
    fetchUser();
  }, []);

  useEffect(() => {
    localStorage.setItem('savedJobs', JSON.stringify(savedJobs));
  }, [savedJobs]);

  const fetchUser = async () => {
    try {
      const data = await getCurrentUser();
      setUser(data);
      // Load user's existing postulations to know which ones they already applied to
      try {
        const myPostulations = await getPostulationsByWorker();
        const ids = new Set(myPostulations.map(p => p.offer?.id).filter(Boolean));
        setAppliedOfferIds(ids);
      } catch (e) {
        // user might not be a worker
      }
    } catch (err) {
      console.error("Error usuario", err);
    }
  };

  const fetchJobs = async () => {
    try {
      const data = await getAllOffers();
      const mapped = data.map(o => ({
        id: o.id,
        title: o.title,
        company: "Empresa #" + o.employerId,
        employerId: o.employerId,
        type: o.category || "General",
        description: o.description,
        location: o.location,
        salary: o.salaryRange ? `$${o.salaryRange?.toLocaleString('es-CO')}` : "No especificado",
        salaryRaw: o.salaryRange || 0,
        timePosted: formatDate(o.publicationDate),
        category: o.category || "Otro",
        rawDate: o.publicationDate,
        status: o.status || "OPEN"
      }));

      mapped.sort((a, b) => {
        if (!a.rawDate) return 1;
        if (!b.rawDate) return -1;
        return new Date(b.rawDate) - new Date(a.rawDate);
      });

      setJobs(mapped);

      // Fetch employer ratings for unique employer IDs
      const uniqueEmployerIds = [...new Set(data.map(o => o.employerId).filter(Boolean))];
      const ratingsMap = {};
      await Promise.all(
        uniqueEmployerIds.map(async (eid) => {
          try {
            const reviews = await getReviewsByUser(eid);
            if (Array.isArray(reviews) && reviews.length > 0) {
              const avg = reviews.reduce((sum, r) => sum + (r.rating || 0), 0) / reviews.length;
              ratingsMap[eid] = parseFloat(avg.toFixed(1));
            }
          } catch (e) {
            // employer has no reviews
          }
        })
      );
      setEmployerRatings(ratingsMap);
    } catch (err) {
      console.error("Error cargando ofertas", err);
      showToast("Error cargando ofertas", "error");
    }
  };

  const isAlreadyApplied = (jobId) => appliedOfferIds.has(jobId);

  const handleApply = async (job) => {
    if (applying) return;

    // Check if offer is closed
    if (job.status === "CLOSED") {
      showToast("Esta oferta se encuentra cerrada por el momento", "error");
      return;
    }

    // Check if already applied
    if (isAlreadyApplied(job.id)) {
      showToast("Ya te has postulado a esta oferta", "info");
      return;
    }

    if (!user) {
      showToast("Usuario no cargado", "error");
      return;
    }

    // Open the test first — postulation will be created after the test completes
    setTestJob(job);
    setLoadingTest(true);

    setTimeout(() => {
      setLoadingTest(false);
      setTestOpen(true);
    }, 1500);
  };

  const handleTestComplete = async (result) => {
    setTestOpen(false);
    setTestResult(result);
    setTestResultOpen(true);

    // Now create the postulation with the test score
    if (testJob && user) {
      try {
        setApplying(true);
        await createPostulation(testJob.id, user.id, Math.round(result.score));
        setAppliedOfferIds(prev => new Set([...prev, testJob.id]));
        showToast("✅ Postulación enviada con tu calificación", "success");
      } catch (err) {
        console.error("Error al crear postulación", err);
        showToast("Error al enviar la postulación", "error");
      } finally {
        setApplying(false);
      }
    }
  };

  const handleSaveJob = (job) => {
    const exists = savedJobs.find(j => j.id === job.id);
    if (exists) {
      setSavedJobs(prev => prev.filter(j => j.id !== job.id));
    } else {
      setSavedJobs(prev => [...prev, job]);
    }
  };

  const handleRemoveSaved = (jobId) => {
    setSavedJobs(prev => prev.filter(j => j.id !== jobId));
  };

  const isJobSaved = (jobId) => savedJobs.some(j => j.id === jobId);

  const handleApplyFromDetails = async () => {
    if (selectedJob) {
      await handleApply(selectedJob);
      setSelectedJob(null);
    }
  };

  const formatDate = (date) => {
    if (!date) return "Reciente";
    const diff = (new Date() - new Date(date)) / 1000;
    if (diff < 3600) return "Hace unos minutos";
    if (diff < 86400) return `Hace ${Math.floor(diff / 3600)} horas`;
    return `Hace ${Math.floor(diff / 86400)} días`;
  };

  // Filtrado
  const filteredJobs = jobs.filter(job => {
    const matchesSearch = searchText === '' ||
      job.title.toLowerCase().includes(searchText.toLowerCase()) ||
      job.company.toLowerCase().includes(searchText.toLowerCase()) ||
      (job.location || '').toLowerCase().includes(searchText.toLowerCase());

    const matchesCategory = selectedCategory === 'Todas las categorías' || job.category === selectedCategory;
    const matchesType = selectedType === 'Todos los tipos' || job.type === selectedType;
    const matchesLocation = selectedLocation === 'Todas las ubicaciones' || job.location === selectedLocation;

    const salaryRange = SALARY_RANGES[selectedSalaryRange];
    const matchesSalary = job.salaryRaw >= salaryRange.min && job.salaryRaw <= salaryRange.max;

    return matchesSearch && matchesCategory && matchesType && matchesLocation && matchesSalary;
  });

  return (
    <>
      <div className="page-header">
        <h1 className="page-title">Buscar Trabajo</h1>
        <p className="page-subtitle">Descubre {filteredJobs.length} oportunidades laborales</p>
      </div>

      <div className="search-filters">
        <div className="search-input-wrapper">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            placeholder="Buscar por título, empresa o ubicación..."
            className="dash-search-input"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
          />
        </div>

        <select className="dash-select" value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
          {CATEGORIES.map(cat => <option key={cat} value={cat}>{cat}</option>)}
        </select>

        <select className="dash-select" value={selectedType} onChange={(e) => setSelectedType(e.target.value)}>
          {TYPES.map(type => <option key={type} value={type}>{type}</option>)}
        </select>

        <select className="dash-select" value={selectedLocation} onChange={(e) => setSelectedLocation(e.target.value)}>
          {LOCATIONS.map(loc => <option key={loc} value={loc}>{loc}</option>)}
        </select>

        <select className="dash-select" value={selectedSalaryRange} onChange={(e) => setSelectedSalaryRange(Number(e.target.value))}>
          {SALARY_RANGES.map((range, i) => <option key={i} value={i}>{range.label}</option>)}
        </select>

        <Button variant="secondary" className="saved-btn" onClick={() => setSavedOpen(true)}>
          <Bookmark size={18} /> Guardados ({savedJobs.length})
        </Button>
      </div>

      <div className="job-list">
        {filteredJobs.length === 0 ? (
          <p>No hay ofertas disponibles</p>
        ) : (
          filteredJobs.map(job => (
            <JobCard
              key={job.id}
              job={job}
              onClick={() => setSelectedJob(job)}
              onApply={() => handleApply(job)}
              isSaved={isJobSaved(job.id)}
              onSave={() => handleSaveJob(job)}
              applying={applying}
              isApplied={isAlreadyApplied(job.id)}
              isClosed={job.status === "CLOSED"}
              employerRating={employerRatings[job.employerId] || null}
            />
          ))
        )}
      </div>

      <JobDetailsModal
        isOpen={!!selectedJob}
        onClose={() => setSelectedJob(null)}
        job={selectedJob}
        onApply={handleApplyFromDetails}
        isSaved={selectedJob ? isJobSaved(selectedJob.id) : false}
        onSave={() => selectedJob && handleSaveJob(selectedJob)}
        applying={applying}
        isApplied={selectedJob ? isAlreadyApplied(selectedJob.id) : false}
        isClosed={selectedJob?.status === "CLOSED"}
        employerRating={selectedJob ? (employerRatings[selectedJob.employerId] || null) : null}
      />

      <SavedJobsModal
        isOpen={savedOpen}
        onClose={() => setSavedOpen(false)}
        savedJobs={savedJobs}
        onRemove={handleRemoveSaved}
        onViewDetails={(job) => { setSavedOpen(false); setSelectedJob(job); }}
      />

      {/* Loading test overlay */}
      {loadingTest && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)',
          backdropFilter: 'blur(4px)', display: 'flex', alignItems: 'center',
          justifyContent: 'center', zIndex: 100, flexDirection: 'column', gap: '1rem'
        }}>
          <Loader2 size={44} style={{ color: 'white', animation: 'spin 1s linear infinite' }} />
          <p style={{ color: 'white', fontSize: '1.2rem', fontWeight: 600, margin: 0 }}>Cargando test...</p>
          <p style={{ color: 'rgba(255,255,255,0.7)', fontSize: '0.9rem', margin: 0 }}>Preparando tu evaluación</p>
        </div>
      )}

      <TestModal
        isOpen={testOpen}
        onClose={() => setTestOpen(false)}
        onComplete={handleTestComplete}
        jobCategory={testJob?.category || testJob?.type || 'General'}
      />

      <TestResultModal
        isOpen={testResultOpen}
        onClose={() => { setTestResultOpen(false); setTestResult(null); }}
        result={testResult}
      />
    </>
  );
}